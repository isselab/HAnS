package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.vpIntegration.FeaturesHandler;
import se.isselab.HAnS.vpIntegration.TracingHandler;


public class PasteAsset extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        VirtualFile targetVirtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (targetVirtualFile == null) return;
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        boolean isDirectory = checkPsiDirectory(targetVirtualFile);
        PsiManager psiManager = PsiManager.getInstance(anActionEvent.getProject());
        PsiDirectory targetDirectory = psiManager.findDirectory(targetVirtualFile);
        if (isDirectory) {
            if (CloneAsset.clonedFile != null && CloneAsset.clonedDirectory == null) {
                addClonedFile(project, targetDirectory);
                featuresHandler.addFeaturesToFeatureModel();
                CloneAsset.clonedFile = null;
            } else if (CloneAsset.clonedFile == null && CloneAsset.clonedDirectory != null) {
                pasteClonedDirectory(anActionEvent, project, targetDirectory);
                CloneAsset.clonedDirectory = null;
            }
        }
        tracingHandler.storeFileOrFolderTrace();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabledAndVisible(false);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isDirectory = checkPsiDirectory(virtualFile);
        boolean clonedAssetNotNull = (CloneAsset.clonedFile != null || CloneAsset.clonedDirectory != null);
        e.getPresentation().setEnabledAndVisible(isDirectory && clonedAssetNotNull);
    }

    private void pasteClonedDirectory(AnActionEvent anActionEvent, Project project, PsiDirectory targetDirectory) {
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        String newDirectoryName = CloneAsset.clonedDirectory.getName();
        PsiDirectory newDirectory = createDirectory(targetDirectory, newDirectoryName, project);
        for (PsiFile file : CloneAsset.clonedDirectory.getFiles()) {
            CloneAsset.clonedFile = file;
            addClonedFile(project, newDirectory);
            featuresHandler.addFeaturesToFeatureModel();
            CloneAsset.clonedFile = null;
        }
        for (PsiDirectory subDir : CloneAsset.clonedDirectory.getSubdirectories()) {
            targetDirectory = newDirectory;
            CloneAsset.clonedDirectory = subDir;
            pasteClonedDirectory(anActionEvent, project, targetDirectory);
        }
    }

    private PsiDirectory createDirectory(PsiDirectory targetDirectory, String newDirectoryName, Project project) {
        final PsiDirectory[] newDirectory = new PsiDirectory[1];
        WriteCommandAction.runWriteCommandAction(project, () -> {
            newDirectory[0] = targetDirectory.createSubdirectory(newDirectoryName);
        });
        return newDirectory[0];
    }

    public boolean checkPsiDirectory(VirtualFile vf) {
        boolean isDirectory = vf != null && vf.isDirectory();
        return isDirectory;
    }

    public void addClonedFile(Project project, PsiDirectory targetDirectory) {
        if (targetDirectory != null) {
            WriteCommandAction.runWriteCommandAction(project, new Runnable() {
                @Override
                public void run() {
                    targetDirectory.add(CloneAsset.clonedFile);
                }
            });
        }
    }
}
