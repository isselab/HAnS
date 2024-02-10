package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.rd.util.AtomicReference;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.vpIntegration.FeaturesHandler;
import se.isselab.HAnS.vpIntegration.TracingHandler;

import java.util.List;


public class PasteClonedAsset extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String place = anActionEvent.getPlace();
        Project project = anActionEvent.getProject();
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
        if(place.equals("ProjectViewPopup")){
            //handle Project menu
            handleProjectMenu(anActionEvent, project, tracingHandler);

        } else if(place.equals("EditorPopup")){
            //handle editor menu
            handleEditorMenu(anActionEvent, project, tracingHandler);
        }
        CloneAsset.resetClones();
    }

    private void handleEditorMenu(AnActionEvent anActionEvent, Project project,TracingHandler tracingHandler) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null || project == null) return;
        PsiFile currentFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (currentFile == null) return;
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        if(CloneAsset.elementsInRange != null){
            pasteClonedCodeBlock(editor, project, currentFile);
            featuresHandler.addFeaturesToFeatureModel();
        } else if(CloneAsset.clonedClass != null){
            pasteClonedClass(editor, project, currentFile);
            featuresHandler.addFeaturesToFeatureModel();
        } else if(CloneAsset.clonedMethod != null){
            pasteClonedMethod(editor, project, currentFile);
            featuresHandler.addFeaturesToFeatureModel();
        }
        tracingHandler.storeCodeAssetsTrace();
    }

    private void handleProjectMenu(AnActionEvent anActionEvent, Project project, TracingHandler tracingHandler) {
        VirtualFile targetVirtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (targetVirtualFile == null) return;
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        boolean isDirectory = checkPsiDirectory(targetVirtualFile);
        PsiManager psiManager = PsiManager.getInstance(anActionEvent.getProject());
        PsiDirectory targetDirectory = psiManager.findDirectory(targetVirtualFile);
        if (isDirectory) {
            if (CloneAsset.clonedFile != null && CloneAsset.clonedDirectory == null) {
                addClonedFile(project, targetDirectory);
                featuresHandler.addFeaturesToFeatureModel();
                pasteFeatureAnnotations(targetDirectory);
            } else if (CloneAsset.clonedFile == null && CloneAsset.clonedDirectory != null) {
                pasteClonedDirectory(anActionEvent, project, targetDirectory);
            }
        }
        tracingHandler.storeFileOrFolderTrace();
    }

    private void pasteFeatureAnnotations(PsiDirectory targetDirectory) {
        if(CloneAsset.featuresAnnotations != null)
            pasteToFeatureToFile(targetDirectory);
    }

    private void pasteToFeatureToFile(PsiDirectory psiDirectory) {
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
            fileContent.append(CloneAsset.clonedFile.getName()).append("\n");
            for (int i = 0; i < CloneAsset.featuresAnnotations.size(); i++) {
                if(i == CloneAsset.featuresAnnotations.size() -1 ){
                    fileContent.append(CloneAsset.featuresAnnotations.get(i).getText()).append("\n");
                } else {
                    fileContent.append(CloneAsset.featuresAnnotations.get(i).getText()).append(", ");
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

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabledAndVisible(false);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isDirectory = checkPsiDirectory(virtualFile);
        boolean clonedAssetNotNull = (CloneAsset.clonedFile != null || CloneAsset.clonedDirectory != null || CloneAsset.clonedClass != null || CloneAsset.clonedMethod != null || CloneAsset.elementsInRange != null);
        e.getPresentation().setEnabledAndVisible(clonedAssetNotNull);
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

    private void pasteClonedMethod(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiMethod clonedMethodForInsertion = (PsiMethod) CloneAsset.clonedMethod.copy();
            pasteTarget.getParent().addBefore(clonedMethodForInsertion, pasteTarget);
        });
    }

    private void pasteClonedClass(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiClass clonedClassForInsertion = (PsiClass) CloneAsset.clonedClass.copy();
            pasteTarget.getParent().addBefore(clonedClassForInsertion, pasteTarget);
        });
    }

    private void pasteClonedCodeBlock(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        List<PsiElement> elementsToPaste = CloneAsset.elementsInRange;
        if (elementsToPaste == null || elementsToPaste.isEmpty()) return;
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");  // Start of code block
        for (PsiElement element : elementsToPaste) {
            sb.append(element.getText());
        }
        sb.append("}");
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
            PsiElement codeBlock = elementFactory.createCodeBlockFromText(sb.toString(), null);
            if (pasteTarget != null && pasteTarget.isValid()) {
                pasteTarget.getParent().addBefore(codeBlock, pasteTarget);
            }
        });

    }

    private PsiElement getTargetPaste(Editor editor, PsiFile currentFile){
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement pasteTarget = currentFile.findElementAt(caretOffset);
        if (pasteTarget == null) return null;
        return pasteTarget;
    }
}
