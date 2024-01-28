package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class PasteAsset extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        VirtualFile targetVirtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (targetVirtualFile == null) return;
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
        boolean isDirectory = checkPsiDirectory(targetVirtualFile);
        PsiManager psiManager = PsiManager.getInstance(anActionEvent.getProject());
        PsiDirectory targetDirectory = psiManager.findDirectory(targetVirtualFile);
        if(isDirectory){
            if(CloneAsset.clonedFile != null && CloneAsset.clonedDirectory == null){
                addClonedFile(project, targetDirectory);
                addFeaturesToFeatureModel(project);
                CloneAsset.clonedFile = null;
            }else if(CloneAsset.clonedFile == null && CloneAsset.clonedDirectory != null){
                pasteClonedDirectory(anActionEvent, project, targetDirectory);
                CloneAsset.clonedDirectory = null;
            }
        }
        tracingHandler.storeFileOrFolderTrace();
    }

    @Override
    public void update(@NotNull AnActionEvent e){
        super.update(e);
        e.getPresentation().setEnabledAndVisible(false);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isDirectory = checkPsiDirectory(virtualFile);
        boolean clonedAssetNotNull = (CloneAsset.clonedFile != null || CloneAsset.clonedDirectory != null);
        e.getPresentation().setEnabledAndVisible(isDirectory && clonedAssetNotNull);
    }

    private void pasteClonedDirectory(AnActionEvent anActionEvent, Project project, PsiDirectory targetDirectory) {
        String newDirectoryName = CloneAsset.clonedDirectory.getName();
        PsiDirectory newDirectory = createDirectory(targetDirectory, newDirectoryName, project);
        for (PsiFile file : CloneAsset.clonedDirectory.getFiles()) {
            CloneAsset.clonedFile = file;
            addClonedFile(project, newDirectory);
            addFeaturesToFeatureModel(project);
            CloneAsset.clonedFile = null;
        }
        for (PsiDirectory subDir : CloneAsset.clonedDirectory.getSubdirectories()) {
            targetDirectory = newDirectory;
            CloneAsset.clonedDirectory = subDir;
            pasteClonedDirectory(anActionEvent, project,targetDirectory);
        }
    }

    private PsiDirectory createDirectory(PsiDirectory targetDirectory, String newDirectoryName, Project project) {
        final PsiDirectory[] newDirectory = new PsiDirectory[1];
        WriteCommandAction.runWriteCommandAction(project, () -> {
            newDirectory[0] = targetDirectory.createSubdirectory(newDirectoryName);
        });
        return newDirectory[0];
    }

    public boolean checkPsiDirectory(VirtualFile vf){
        boolean isDirectory = vf != null && vf.isDirectory();
        return isDirectory;
    }

    public void addClonedFile(Project project, PsiDirectory targetDirectory){
        if (targetDirectory != null) {
            WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                @Override
                public void run() {
                    targetDirectory.add(CloneAsset.clonedFile);
                }
            });
        }
    }

    public void addFeaturesToFeatureModel(Project project){
        List<String> clonedFeatureNames = CloneAsset.featureNames;
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
}
