package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFeatureNameImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFileAnnotationImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFileNameImpl;
import se.isselab.HAnS.folderAnnotation.psi.impl.FolderAnnotationLpqImpl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeatureModelHandler {
    Project project;
    public FeatureModelHandler(Project project){
        this.project = project;
    }

    public void addFeaturesToFeatureModel(){
        List<String> clonedFeatureNames = FeaturesCodeAnnotations.getInstance().getFeatureNames();
        List<String> modifiedFeatureNames = new ArrayList<>();
        VirtualFile featureModelFile = findFeatureModelFile(project);

        if (featureModelFile != null) {
            String content = readFileContent(featureModelFile);
            assert content != null : "Feature model empty";
            Set<String> existingFeatures = parseExistingFeatures(content);

            boolean modified = false;
            StringBuilder newContent = new StringBuilder(content);
            if(clonedFeatureNames != null){
                for (String featureName : clonedFeatureNames) {
                    if (!existingFeatures.contains(featureName)) {
                        if (!existingFeatures.contains("UNASSIGNED")) {
                            createUnassignedFeature(newContent);
                            existingFeatures.add("UNASSIGNED");
                            modified = true;
                        }
                        addFeatureUnderUnassigned(newContent, featureName);
                        featureName = "UNASSIGNED::" + featureName;
                        modified = true;
                    }
                    modifiedFeatureNames.add(featureName);
                }
            }
            if (modified) {
                writeBackToFile(featureModelFile, newContent.toString(), project);
                FeaturesCodeAnnotations.getInstance().setFeatureNames(modifiedFeatureNames);
            }
        }
    }

    private VirtualFile findFeatureModelFile(Project project) {
        VirtualFile projectBaseDir = project.getBaseDir();
        if (projectBaseDir == null) {
            return null;
        }
        final VirtualFile[] foundFile = new VirtualFile[1];
        // VfsUtil to visit each file and directory starting from the base directory
        VfsUtil.visitChildrenRecursively(projectBaseDir, new VirtualFileVisitor<Void>() {
            @Override
            public boolean visitFile(@org.jetbrains.annotations.NotNull VirtualFile file) {
                if (file.getName().endsWith(".feature-model")) {
                    foundFile[0] = file;
                    return false;
                }
                return true; // Continue searching
            }
        });
        return foundFile[0];
    }

    private String readFileContent(VirtualFile file) {
        Document document = getDocument(file);
        if (document == null) {
            return null;
        }
        return document.getText();
    }

    private Document getDocument(VirtualFile file) {
        final Document[] documents = new Document[1];
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                documents[0] = FileDocumentManager.getInstance().getDocument(file);
            }
        });
        return documents[0];
    }

    private Set<String> parseExistingFeatures(String content) {
        Set<String> features = new HashSet<>();
        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) {
                features.add(line);
            }
        }
        scanner.close();
        return features;
    }

    private void createUnassignedFeature(StringBuilder content) {
        content.append("\nUNASSIGNED");
    }

    private void addFeatureUnderUnassigned(StringBuilder content, String featureName) {
        String unassignedFeaturePattern = "\nUNASSIGNED";
        int unassignedIndex = content.indexOf(unassignedFeaturePattern);
        if (unassignedIndex != -1) {
            int insertPosition = content.indexOf("\n", unassignedIndex + unassignedFeaturePattern.length());
            if (insertPosition == -1) {
                insertPosition = content.length();
            }
            content.insert(insertPosition, "\n\t" + featureName);
        } else {
            createUnassignedFeature(content);
            content.append("\n\t").append(featureName);
        }
    }

    private void writeBackToFile(VirtualFile file, String content, Project project) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                document.setText(content);
            }
        });
    }

    public ArrayList<PsiElement> findFeatureToFileMappings(PsiFile psifile){
        ArrayList<PsiElement> featuresElements = new ArrayList<>();
        PsiDirectory parentDirectory = psifile.getParent();
        PsiFile featureFile = null;
        if (parentDirectory != null) {
            for (PsiFile file : parentDirectory.getFiles()) {
                if (file.getName().endsWith(".feature-to-file")) {
                    featureFile = file;
                    break;
                }
            }
        }
        if(featureFile != null){
            featureFile.accept(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    if (element instanceof FileAnnotationFileNameImpl) {
                        if(element.getText().equals(psifile.getName())){
                            PsiElement desiredParent = PsiTreeUtil.getParentOfType(element, FileAnnotationFileAnnotationImpl.class);
                            if (desiredParent != null) {
                                Collection<PsiElement> children = PsiTreeUtil.findChildrenOfType(desiredParent, FileAnnotationFeatureNameImpl.class);
                                for (PsiElement child : children) {
                                    featuresElements.add(child);
                                }
                            }
                        }
                    }
                }
            });
        }
        if(!featuresElements.isEmpty())
            return featuresElements;
        return null;
    }
    public ArrayList<PsiElement> findFeatureToFolderMappings(PsiDirectory psiDirectory){
        ArrayList<PsiElement> featuresElements = new ArrayList<>();
        PsiFile featureFolder = null;
        PsiFile[] psifiles = getDirectoryPsiFiles(psiDirectory);
        if (psiDirectory != null) {
            for (PsiFile file : psifiles) {
                if (file.getName().endsWith(".feature-to-folder")) {
                    featureFolder = file;
                    break;
                }
            }
        }
        if(featureFolder != null){
            featureFolder.accept(new PsiRecursiveElementVisitor() {
                @Override
                public void visitElement(PsiElement element) {
                    super.visitElement(element);
                    if (element instanceof FolderAnnotationLpqImpl) {
                            featuresElements.add(element);
                        }
                    }
            });
        }
        if(!featuresElements.isEmpty())
            return featuresElements;
        return null;
    }

    private PsiFile[] getDirectoryPsiFiles(PsiDirectory dir) {
        final PsiFile[][] files = new PsiFile[1][];
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                files[0] = dir.getFiles();
            }
        });
        return files[0];
    }
    public static List<String> getFeaturesAnnotationsFromText(String copiedText){
        List<String> features = new ArrayList<>();
        String regex = "// &(?:line|begin)\\[([^\\]]+)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(copiedText);
        while (matcher.find()) {
            String[] featureParts = matcher.group(1).split("::");
            for (String part : featureParts) {
                if (!part.isEmpty() && !features.contains(part)) {
                    features.add(part);
                }
            }
        }
        if(!features.isEmpty())
            return features;
        return null;
    }

}

