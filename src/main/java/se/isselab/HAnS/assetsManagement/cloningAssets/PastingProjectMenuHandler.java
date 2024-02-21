package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.rd.util.AtomicReference;

public class PastingProjectMenuHandler {
    public static void handleProjectMenu(AnActionEvent anActionEvent, Project project, TracingHandler tracingHandler) {
        VirtualFile targetVirtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (targetVirtualFile == null) return;
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        boolean isDirectory = checkPsiDirectory(targetVirtualFile);
        PsiManager psiManager = PsiManager.getInstance(anActionEvent.getProject());
        PsiDirectory targetDirectory = psiManager.findDirectory(targetVirtualFile);
        if (isDirectory) {
            if (AssetsToClone.clonedFile != null && AssetsToClone.clonedDirectory == null) {
                pasteClonedFile(project, targetDirectory);
                featuresHandler.addFeaturesToFeatureModel();
                pasteFeatureAnnotations(targetDirectory);
            } else if (AssetsToClone.clonedFile == null && AssetsToClone.clonedDirectory != null) {
                pasteClonedDirectory(anActionEvent, project, targetDirectory);
            }
        }
        tracingHandler.storeFileOrFolderTrace();
    }
    private static void pasteClonedDirectory(AnActionEvent anActionEvent, Project project, PsiDirectory targetDirectory) {
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        String newDirectoryName = AssetsToClone.clonedDirectory.getName();
        PsiDirectory newDirectory = createDirectory(targetDirectory, newDirectoryName, project);
        for (PsiFile file : AssetsToClone.clonedDirectory.getFiles()) {
            AssetsToClone.clonedFile = file;
            pasteClonedFile(project, newDirectory);
            featuresHandler.addFeaturesToFeatureModel();
            AssetsToClone.clonedFile = null;
        }
        for (PsiDirectory subDir : AssetsToClone.clonedDirectory.getSubdirectories()) {
            targetDirectory = newDirectory;
            AssetsToClone.clonedDirectory = subDir;
            pasteClonedDirectory(anActionEvent, project, targetDirectory);
        }
    }

    private static void pasteClonedFile(Project project, PsiDirectory targetDirectory) {
        if (targetDirectory != null) {
            WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                @Override
                public void run() {
                    targetDirectory.add(AssetsToClone.clonedFile);
                }
            });
        }
    }

    private static void pasteFeatureAnnotations(PsiDirectory targetDirectory) {
        if(AssetsToClone.featuresAnnotations != null)
            pasteToFeatureToFile(targetDirectory);
    }

    private static void pasteToFeatureToFile(PsiDirectory psiDirectory) {
        AtomicReference<PsiFile> fileMappingRef = new AtomicReference<>(null);
        Project project = psiDirectory.getProject();
        if (psiDirectory != null) {
            for (PsiFile file : psiDirectory.getFiles()) {
                if (file.getName().endsWith(".feature-to-file")) {
                    fileMappingRef.getAndSet(file);
                    break;
                }
            }
        }
        if (fileMappingRef.get() == null) {
            String fileName = ".feature-to-file";
            PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
            PsiFile newFile = fileFactory.createFileFromText(fileName, PlainTextFileType.INSTANCE, "");
            WriteCommandAction.runWriteCommandAction(project , () -> {
                PsiFile addedFile = (PsiFile) psiDirectory.add(newFile);
                fileMappingRef.getAndSet(addedFile);
            });

        }

        WriteCommandAction.runWriteCommandAction(project, () -> {
            StringBuilder fileContent = new StringBuilder();
            fileContent.append(AssetsToClone.clonedFile.getName()).append("\n");
            for (int i = 0; i < AssetsToClone.featuresAnnotations.size(); i++) {
                if(i == AssetsToClone.featuresAnnotations.size() -1 ){
                    fileContent.append(AssetsToClone.featuresAnnotations.get(i).getText()).append("\n");
                } else {
                    fileContent.append(AssetsToClone.featuresAnnotations.get(i).getText()).append(", ");
                }
            }
            PsiFile fileMappingFile = fileMappingRef.get();
            if (fileMappingFile != null && fileMappingFile.getViewProvider().getDocument() != null) {
                var document = fileMappingFile.getViewProvider().getDocument();
                if(document != null){
                    String existingContent = document.getText();
                    String newContent = existingContent + fileContent.toString();
                    document.setText(newContent);
                    CodeStyleManager.getInstance(project).reformat(fileMappingFile);
                }
            }
        });
    }

    private static boolean checkPsiDirectory(VirtualFile vf) {
        boolean isDirectory = vf != null && vf.isDirectory();
        return isDirectory;
    }
    private static PsiDirectory createDirectory(PsiDirectory targetDirectory, String newDirectoryName, Project project) {
        final PsiDirectory[] newDirectory = new PsiDirectory[1];
        WriteCommandAction.runWriteCommandAction(project, () -> {
            newDirectory[0] = targetDirectory.createSubdirectory(newDirectoryName);
        });
        return newDirectory[0];
    }
}
