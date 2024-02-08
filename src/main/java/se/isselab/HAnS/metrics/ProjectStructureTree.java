package se.isselab.HAnS.metrics;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.PsiManagerImpl;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationVisitor;
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

    private enum Type {FOLDER, FILE, LINE}

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

    // Method to process the project structure
    public ProjectStructureTree processProjectStructure(Project project, String rootFolderPath) {

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
            LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
            VirtualFile virtualFile = localFileSystem.findFileByIoFile(specialFileNode);
            if (virtualFile != null) {
                PsiFile foundFile = PsiManagerImpl.getInstance(project).findFile(virtualFile);
                if (foundFile != null) {
                    processFeatureToFile(foundFile, parent);
                }
            }

        }
    }

    private void processFile(Project project, File file, ProjectStructureTree parent, Queue<File> featureToFileQueue) {
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile virtualFile = localFileSystem.findFileByIoFile(file);
        if (virtualFile == null) { return; }

        PsiFile foundFile = PsiManagerImpl.getInstance(project).findFile(virtualFile);

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

            this.processCode(project, file, fileNode);
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
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance();
        VirtualFile virtualFile = localFileSystem.findFileByIoFile(file);
        PsiFile foundFile = PsiManagerImpl.getInstance(project).findFile(virtualFile);
        AtomicReference<Set<String>> featureLPQs = new AtomicReference<>(new HashSet<>());

        if (foundFile != null) {
            foundFile.accept(new CodeAnnotationVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof PsiComment) {
                        String comment = element.getText();
                        if (comment.contains("&begin") || comment.contains("&end") || comment.contains("&line")) {
                            featureLPQs.updateAndGet(
                                    currentList -> {
                                        currentList.addAll(extractLPQsFromInlineAnnotation(comment));
                                        return currentList;
                                    });
                        }
                    }
                    super.visitElement(element);
                }
            });

            Map<String, Integer> featureDepths = new HashMap<>();
            if (!featureLPQs.get().isEmpty()) {
                for (String feature: featureLPQs.get()) {
                    List<Integer> linesWithFeature = locateCodeAnnotationLinesForFeature(file.getPath(),feature);
                    for (int targetLineNumber : linesWithFeature) {
                        int depth = countCodeAnnotationDepth(file.getPath(), targetLineNumber, parent.getDepth());
                        featureDepths.put(feature, depth);
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : featureDepths.entrySet()) {
                ProjectStructureTree pst = new ProjectStructureTree(entry.getKey(), parent.getPath(), Type.LINE, entry.getValue());
                pst.featureList.add(entry.getKey());
                parent.children.add(pst);
            }

        }
    }

    // takes feature-to-code annotation and returns set of feature-LPQs
    private static Set<String> extractLPQsFromInlineAnnotation(String input) {
        Set<String> featureList = new HashSet<>();
        Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]");

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String content = matcher.group(1);

            // Split the content into individual features using a comma as the delimiter
            String[] featureArray = content.split(",\\s*");

            for (String feature : featureArray) {
                featureList.add(feature.trim());
            }
        }

        return featureList;
    }

    // find all lines with feature-to-code annotations with specific feature within specific file
    private static List<Integer> locateCodeAnnotationLinesForFeature(String filePath, String searchFeature) {
        List<Integer> matchingLines = new ArrayList<>();
        String patternStr = "&(begin|line)\\[(?=.*\\b" + searchFeature + "\\b)([^\\]]+)\\]";
        Pattern pattern= Pattern.compile(patternStr);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {

                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    matchingLines.add(lineNumber);
                }

                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matchingLines;
    }

    // determines depth of a code annotation
    // goes to line containing the code annotation and then moves up and counts other parent code annotations
    private static int countCodeAnnotationDepth(String filePath, int targetLineNumber, int parentFileDepth) {
        int beginOccurrences = 0;
        int endOccurrences = 0;

        String patternStringBegin = "&begin\\[\\s*([a-zA-Z]+)\\s*(?:,\\s*([a-zA-Z]+)\\s*)?\\]";
        String patternStringEnd = "&end\\[\\s*([a-zA-Z]+)\\s*(?:,\\s*([a-zA-Z]+)\\s*)?\\]";
        Pattern patternBegin = Pattern.compile(patternStringBegin);
        Pattern patternEnd = Pattern.compile(patternStringEnd);


        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1;

            while ((line = br.readLine()) != null && lineNumber < targetLineNumber) {
                Matcher matcher = patternBegin.matcher(line);
                if (matcher.find()) {
                    // Extract the matched part of the line
                    String matchedPart = matcher.group(0);
                    long commaCount = matchedPart.chars().filter(ch -> ch == ',').count() + 1;
                    beginOccurrences += (int) commaCount;
                }

                matcher = patternEnd.matcher(line);
                if (matcher.find()) {
                    // Extract the matched part of the line
                    String matchedPart = matcher.group(0);
                    long commaCount = matchedPart.chars().filter(ch -> ch == ',').count() + 1;
                    endOccurrences += (int) commaCount;
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return parentFileDepth + ( beginOccurrences - endOccurrences ) + 1;
    }


}
