package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PasteAssetCode extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        Project project = anActionEvent.getProject();
        if (editor == null || project == null) return;
        PsiFile currentFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (currentFile == null) return;
        if(CloneAssetCode.elementsInRange != null){
            pasteClonedCodeBlock(editor, project, currentFile);
        } else if(CloneAssetCode.clonedClass != null){
            pasteClonedClass(editor, project, currentFile);
        } else if(CloneAssetCode.clonedMethod != null){
            pasteClonedMethod(editor, project, currentFile);
        }

    }

    private void pasteClonedMethod(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiMethod clonedMethodForInsertion = (PsiMethod) CloneAssetCode.clonedMethod.copy();
            pasteTarget.getParent().addBefore(clonedMethodForInsertion, pasteTarget);
        });
    }

    private void pasteClonedClass(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            PsiClass clonedClassForInsertion = (PsiClass) CloneAssetCode.clonedClass.copy();
            pasteTarget.getParent().addBefore(clonedClassForInsertion, pasteTarget);
        });
    }

    private void pasteClonedCodeBlock(Editor editor, Project project, PsiFile currentFile) {
        PsiElement pasteTarget = getTargetPaste(editor, currentFile);
        List<PsiElement> elementsToPaste = CloneAssetCode.elementsInRange;
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
