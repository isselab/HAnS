package se.isselab.HAnS.vpIntegration;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFeatureNameImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFileAnnotationImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFileNameImpl;
import se.isselab.HAnS.folderAnnotation.psi.impl.FolderAnnotationLpqImpl;

import java.util.*;

public class FeaturesHandler {
    Project project;
    public FeaturesHandler(Project project){
        this.project = project;
    }

    public void addFeaturesToFeatureModel(){
        List<String> clonedFeatureNames = FeaturesCodeAnnotations.getInstance().getFeatureNames();
        VirtualFile featureModelFile = findFeatureModelFile(project);

        if (featureModelFile != null) {
            String content = readFileContent(featureModelFile);
            Set<String> existingFeatures = parseExistingFeatures(content);

            boolean modified = false;
            StringBuilder newContent = new StringBuilder(content);
            if(clonedFeatureNames != null){
                for (String featureName : clonedFeatureNames) {
                    if (!existingFeatures.contains(featureName)) {
                        if (!existingFeatures.contains("unassigned")) {
                            createUnassignedFeature(newContent);
                            existingFeatures.add("unassigned");
                            modified = true;
                        }
                        addFeatureUnderUnassigned(newContent, featureName);
                        modified = true;
                    }
                }
            }
            if (modified) {
                writeBackToFile(featureModelFile, newContent.toString(), project);
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
        Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document == null) {
            return null;
        }

        return document.getText();
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
        content.append("\nunassigned");
    }

    private void addFeatureUnderUnassigned(StringBuilder content, String featureName) {
        String unassignedFeaturePattern = "\nunassigned";

        // Find the index where the "unassigned" feature is located
        int unassignedIndex = content.indexOf(unassignedFeaturePattern);

        if (unassignedIndex != -1) {
            // Assuming each feature is on a new line and child features are indented
            int insertPosition = content.indexOf("\n", unassignedIndex + unassignedFeaturePattern.length());
            if (insertPosition == -1) {
                // If "unassigned" is the last line, append at the end of the content
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
        if(featuresElements.size() != 0)
            return featuresElements;
        return null;
    }
    public ArrayList<PsiElement> findFeatureToFolderMappings(PsiDirectory psiDirectory){
        ArrayList<PsiElement> featuresElements = new ArrayList<>();
        PsiFile featureFolder = null;
        if (psiDirectory != null) {
            for (PsiFile file : psiDirectory.getFiles()) {
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
        if(featuresElements.size() != 0)
            return featuresElements;
        return null;

    }

}

