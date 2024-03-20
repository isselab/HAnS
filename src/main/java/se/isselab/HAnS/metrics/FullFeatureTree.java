package se.isselab.HAnS.metrics;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiManagerImpl;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReference;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationElementType;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationTokenType;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.intellij.psi.PsiManager.getInstance;
import static com.intellij.psi.search.FilenameIndex.getAllFilesByExt;
import static com.intellij.psi.search.FilenameIndex.getVirtualFilesByName;
import static com.intellij.psi.search.GlobalSearchScope.projectScope;

public class FullFeatureTree {

    private String name;
    private String path;
    private Type type;
    private int depth;
    private HashSet<String> featureList;
    private List<FullFeatureTree> children;

    public enum Type {FOLDER, FILE, LINE}

    public FullFeatureTree() {}

    public FullFeatureTree(String name, String path, Type type, int depth) {
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

    public List<FullFeatureTree> getChildren() {
        return children;
    }

    public static void printTree(FullFeatureTree node, String indent) {
        System.out.println(indent + node.getName() + " " + node.depth + " " + node.featureList.toString()+ " " + node.getType());
        for (FullFeatureTree child : node.getChildren()) {
            printTree(child, indent + "-");
        }
    }

    private static boolean isReadOnly(File file) {
        // Check if the file/folder is read-only
        return !file.canWrite() || file.getPath().endsWith(".class");
    }

    public static FullFeatureTree buildTree(Project project) {
        FullFeatureTree tree = new FullFeatureTree();
        FullFeatureTree result = tree.processProjectStructure(project, getFeatureModelPath(project));
        return result;
    }

    // retrieves the path of the root folder where the .feature-model is located
    private static String getFeatureModelPath(Project project) {
        var allFilenames = getVirtualFilesByName(".feature-model", projectScope(project));
        PsiFile psiFile = null;
        if (!allFilenames.isEmpty()) {
            psiFile = getInstance(project).findFile(allFilenames.iterator().next());
        } else {
            Collection<VirtualFile> virtualFileCollection = getAllFilesByExt(project, "feature-model");
            if (!virtualFileCollection.isEmpty()) {
                psiFile = getInstance(project).findFile(virtualFileCollection.iterator().next());
            }
        }
        return getFeatureModelRootFolderPath(psiFile);
    }

    // Method to process the project structure
    // returns ProjectStructureTree containing all items (feature, folder, code) and their features
    private FullFeatureTree processProjectStructure(Project project, String rootFolderPath) {

        File rootFolder = new File(rootFolderPath);
        if (!rootFolder.exists() || !rootFolder.isDirectory()) {
            throw new IllegalArgumentException("Invalid root folder path");
        }

        FullFeatureTree root = new FullFeatureTree(
                rootFolder.getName(), rootFolderPath, FullFeatureTree.Type.FOLDER, 0);

        this.processFolder(project, rootFolder, root);

        return root;
    }

    private void processFolder(Project project, File folder, FullFeatureTree parent) {
        Queue<File> specialFilesQueue = new LinkedList<>(); // save .feature-to-file in the folder for later processing
        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                FullFeatureTree folderNode = new FullFeatureTree(
                        file.getName(), file.getAbsolutePath(), FullFeatureTree.Type.FOLDER, parent.depth + 1);
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

    private void processFile(Project project, File file, FullFeatureTree parent, Queue<File> featureToFileQueue) {
        PsiFile foundFile = fileToPsi(project, file);

        if (foundFile instanceof FileAnnotationFile) { // Logic for .feature-to-file
            featureToFileQueue.add(file);
        } else if (foundFile instanceof FolderAnnotationFile) { // Logic for .feature-to-folder
            AtomicReference<String> featureLpq = new AtomicReference<>("");

            foundFile.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element.getNode().getElementType() instanceof FolderAnnotationTokenType) {
                        featureLpq.set(featureLpq.get()+element.getText());
                    }
                    if (!(element.getNode().getElementType() instanceof FolderAnnotationTokenType) &&
                        !(element.getNode().getElementType() instanceof FolderAnnotationElementType)) {
                        if (featureLpq.get().trim().length()>0) {parent.featureList.add(featureLpq.get().trim());}
                        featureLpq.set("");
                    }
                    super.visitElement(element);
                }
            });
            if (featureLpq.get().trim().length()>0) {parent.featureList.add(featureLpq.get().trim());}
        } else {
            FullFeatureTree fileNode = new FullFeatureTree(
                    file.getName(), file.getAbsolutePath(), Type.FILE, parent.depth);
            parent.children.add(fileNode);

            // don't process automatically generated files since they can't contain inline annotations
            if (!isReadOnly(file)) { // Logic for regular files
                this.processCode(project, file, fileNode);
            }

        }
    }

    // Logic for .feature-to-file
    private static void processFeatureToFile(PsiFile file, FullFeatureTree parent) {
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

            for (FullFeatureTree child : parent.children) {
                if (fileNames.contains(child.getPath())) {
                    child.getFeatureList().addAll(featureSet);
                }
            }

        }
    }

    // process code annotations
    private void processCode(Project project, File file, FullFeatureTree parent) {
        PsiFile foundFile = fileToPsi(project, file);
        if (foundFile == null) {
            return;
        }

        // base depth of any child inside the file
        AtomicReference<Integer> lineDepth = new AtomicReference<>(parent.getDepth() + 1);
        foundFile.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                // visit only PsiComments
                if (element instanceof PsiComment) {
                    PsiComment comment = (PsiComment) element;
                    if (comment.getTokenType().toString().equals("END_OF_LINE_COMMENT")) {
                        // iterate over each comment within the file
                        // if comment is a custom language injection, create new
                        // ProjectStructureTree node and add it to the tree
                        InjectedLanguageManager.getInstance(project).enumerate(comment, ((injectedPsi, places) -> {
                            for (PsiLanguageInjectionHost.Shred place : places) {
                                if (place.getHost() == comment) {
                                    for (PsiElement el : injectedPsi.getChildren()) {
                                        if (el instanceof CodeAnnotationLinemarker) {
                                            for (CodeAnnotationLpq lpq : ((CodeAnnotationLinemarker) el).getParameter().getLpqList()) {
                                                FullFeatureTree pst = new FullFeatureTree(lpq.getName(), parent.getPath(), Type.LINE, lineDepth.get());
                                                pst.featureList.add(lpq.getName());
                                                parent.children.add(pst);
                                            }
                                        }
                                        if (el instanceof CodeAnnotationBeginmarker) {
                                            List<CodeAnnotationLpq> lpqList = ((CodeAnnotationBeginmarker) el).getParameter().getLpqList();
                                            for (CodeAnnotationLpq lpq : lpqList) {
                                                FullFeatureTree pst = new FullFeatureTree(lpq.getName(), parent.getPath(), Type.LINE, lineDepth.get());
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

        return PsiManagerImpl.getInstance(project).findFile(virtualFile);
    }

    private static String getFeatureModelRootFolderPath(PsiFile psiFile) {
        if (psiFile!=null) {
            VirtualFile vFile = psiFile.getOriginalFile().getVirtualFile().getParent();
            String path = vFile.getPath();
            return path;
        }
        return null;
    }

}