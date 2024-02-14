package se.isselab.HAnS.vpIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import java.util.List;

public class PastingEditorMenuHandler {
    public static void handleEditorMenu(AnActionEvent anActionEvent, Project project, TracingHandler tracingHandler) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null || project == null) return;
        PsiFile currentFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (currentFile == null) return;
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        if(AssetsToClone.elementsInRange != null){
            pasteClonedCodeBlock(editor, project, currentFile);
            featuresHandler.addFeaturesToFeatureModel();
        } else if(AssetsToClone.clonedClass != null){
            pasteClonedClass(editor, project, currentFile);
            featuresHandler.addFeaturesToFeatureModel();
        } else if(AssetsToClone.clonedMethod != null){
            pasteClonedMethod(editor, project, currentFile);
            featuresHandler.addFeaturesToFeatureModel();
        }
        tracingHandler.storeCodeAssetsTrace();
    }
    private static void pasteClonedMethod(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiMethod clonedMethodForInsertion = (PsiMethod) AssetsToClone.clonedMethod.copy();
            pasteTarget.getParent().addBefore(clonedMethodForInsertion, pasteTarget);
        });
    }

    private static void pasteClonedClass(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiClass clonedClassForInsertion = (PsiClass) AssetsToClone.clonedClass.copy();
            pasteTarget.getParent().addBefore(clonedClassForInsertion, pasteTarget);
        });
    }

    private static void pasteClonedCodeBlock(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        List<PsiElement> elementsToPaste = AssetsToClone.elementsInRange;
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

    private static PsiElement getTargetPaste(Editor editor, PsiFile currentFile){
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement pasteTarget = currentFile.findElementAt(caretOffset);
        if (pasteTarget == null) return null;
        return pasteTarget;
    }
}
