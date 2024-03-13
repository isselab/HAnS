package se.isselab.HAnS.metrics;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageManagerImpl;
import com.intellij.psi.impl.source.tree.injected.InjectedReferenceVisitor;
import com.intellij.psi.injection.ReferenceInjector;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import kotlinx.html.P;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationLinemarkerImpl;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationLpqImpl;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReference;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationTokenType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectStructureTree {

    private String name;
    private String path;
    private Type type;
    private int depth;
    private HashSet<String> featureList;
    private List<ProjectStructureTree> children;

    public enum Type {FOLDER, FILE, LINE}

    public ProjectStructureTree() {}

    public ProjectStructureTree(String name, String path, Type type, int depth) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.depth = depth;
        this.featureList = new HashSet<>();
        this.children = new ArrayList<>();
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getDepth() {
        return depth;
    }

    public HashSet<String> getFeatureList() {
        return featureList;
    }

    public List<ProjectStructureTree> getChildren() {
        return children;
    }

    private static boolean isReadOnly(File file) {
        // Check if the file/folder is read-only
        return !file.canWrite() || file.getPath().endsWith(".class");
    }

    public static ProjectStructureTree buildTree(Project project) {
        ProjectStructureTree tree = new ProjectStructureTree();
        ProjectStructureTree result = tree.processProjectStructure(project, getFeatureModelPath());
        return result;
    }

    private static String getFeatureModelPath() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        return project.getBasePath();
    }

    // Method to process the project structure
    private ProjectStructureTree processProjectStructure(Project project, String rootFolderPath) {

        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid root folder path");
        }

        ProjectStructureTree root = new ProjectStructureTree(
                rootFolder.getName(), rootFolderPath, ProjectStructureTree.Type.FOLDER, 0);

        this.processFolder(project, rootFolder, root);

        return root;
    }

    private void processFolder(Project project, File folder, ProjectStructureTree parent) {
        Queue<File> specialFilesQueue = new LinkedList<>(); // save .feature-to-file in the folder for later processing
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                ProjectStructureTree folderNode = new ProjectStructureTree(
                        file.getName(), file.getAbsolutePath(), ProjectStructureTree.Type.FOLDER, parent.depth + 1);
                parent.children.add(folderNode);
                processFolder(project, file, folderNode);
            } else if (file.isFile()) {
                this.processFile(project, file, parent, specialFilesQueue);
            }
        }

        while (!specialFilesQueue.isEmpty()) {

            File specialFileNode = specialFilesQueue.poll();
            PsiFile foundFile = fileToPsi(project, specialFileNode);
            if (foundFile != null) {
                processFeatureToFile(foundFile, parent);
            }
        }
    }

    private void processFile(Project project, File file, ProjectStructureTree parent, Queue<File> featureToFileQueue) {
        PsiFile foundFile = fileToPsi(project, file);

        if (foundFile instanceof FileAnnotationFile) { // Logic for .feature-to-file
            featureToFileQueue.add(file);
        } else if (foundFile instanceof FolderAnnotationFile) { // Logic for .feature-to-folder
            foundFile.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element.getNode().getElementType() instanceof FolderAnnotationTokenType) {
                        parent.featureList.add(element.getText());
                    }
                    super.visitElement(element);
                }
            });
        } else {
            ProjectStructureTree fileNode = new ProjectStructureTree(
                    file.getName(), file.getAbsolutePath(), Type.FILE, parent.depth);
            parent.children.add(fileNode);

            // don't process automatically generated files since they can't contain inline annotations
            if (!isReadOnly(file)) {
                this.processCode(project, file, fileNode);
            }

        }


    }


    // Logic for .feature-to-file
    private static void processFeatureToFile(PsiFile file, ProjectStructureTree parent) {
        Map<Set<String>, Set<String>> filesToFeatures = new HashMap<>();
        AtomicReference<Set<String>> fileList = new AtomicReference<>(new HashSet<>());
        AtomicReference<Set<String>> featureList = new AtomicReference<>(new HashSet<>());

        file.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {

                if (element instanceof FileAnnotationFileReference) {
                    if (!fileList.get().isEmpty() && !featureList.get().isEmpty()) {
                        filesToFeatures.put(new HashSet<>(fileList.get()), new HashSet<>(featureList.get()));
                        fileList.set(new HashSet<>());
                        featureList.set(new HashSet<>());
                    }
                    Path pathToFile = Paths.get(parent.getPath(), element.getText());
                    fileList.get().add(pathToFile.toString());
                } else if (element instanceof FileAnnotationLpq) {
                    featureList.get().add(element.getText());
                }

                super.visitElement(element);
            }
        });

        if (!fileList.get().isEmpty() && !featureList.get().isEmpty()) {
            filesToFeatures.put(new HashSet<>(fileList.get()), new HashSet<>(featureList.get()));
        }

        for(Map.Entry<Set<String>, Set<String>> entry: filesToFeatures.entrySet() ) {

            Set<String> fileNames = entry.getKey();
            Set<String> featureSet = entry.getValue();

            for (ProjectStructureTree child : parent.children) {
                if (fileNames.contains(child.getPath())) {
                    child.getFeatureList().addAll(featureSet);
                }
            }

        }
    }

    private void processCode(Project project, File file, ProjectStructureTree parent) {
        PsiFile foundFile = fileToPsi(project, file);
        if (foundFile == null) {
            return;
        }

        AtomicReference<Integer> lineDepth = new AtomicReference<>(parent.getDepth() + 1);
        foundFile.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {

                if (element instanceof PsiComment) {
                    PsiComment comment = (PsiComment) element;
                    if (comment.getTokenType().toString().equals("END_OF_LINE_COMMENT")) {
                        InjectedLanguageManager.getInstance(project).enumerate(comment, ((injectedPsi, places) -> {
                            for (PsiLanguageInjectionHost.Shred place : places) {
                                if (place.getHost() == comment) {
                                    for (PsiElement el : injectedPsi.getChildren()) {
                                        if (el instanceof CodeAnnotationLinemarker) {
                                            for (CodeAnnotationLpq lpq : ((CodeAnnotationLinemarker) el).getParameter().getLpqList()) {
                                                ProjectStructureTree pst = new ProjectStructureTree(lpq.getName(), parent.getPath(), Type.LINE, lineDepth.get());
                                                pst.featureList.add(lpq.getName());
                                                parent.children.add(pst);
                                            }
                                        }
                                        if (el instanceof CodeAnnotationBeginmarker) {
                                            List<CodeAnnotationLpq> lpqList = ((CodeAnnotationBeginmarker) el).getParameter().getLpqList();
                                            for (CodeAnnotationLpq lpq : lpqList) {
                                                ProjectStructureTree pst = new ProjectStructureTree(lpq.getName(), parent.getPath(), Type.LINE, lineDepth.get());
                                                pst.featureList.add(lpq.getName());
                                                parent.children.add(pst);
                                            }
                                            lineDepth.set(lineDepth.get() + lpqList.size()); ;
                                        }
                                        if (el instanceof CodeAnnotationEndmarker) {
                                            List<CodeAnnotationLpq> lpqList = ((CodeAnnotationEndmarker) el).getParameter().getLpqList();
                                            lineDepth.set(lineDepth.get() - lpqList.size());
                                        }
                                    }
                                }
                            }
                        }));
                    }

                }
                super.visitElement(element);
            }
        });
    }

    private PsiFile fileToPsi(Project project, File file) {
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile virtualFile = localFileSystem.findFileByIoFile(file);
        if (virtualFile == null) { return null; }

        PsiFile foundFile = PsiManagerImpl.getInstance(project).findFile(virtualFile);
        if (foundFile == null) { return null; }
        return foundFile;
    }
}