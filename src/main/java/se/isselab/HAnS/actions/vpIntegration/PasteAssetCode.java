package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PasteAssetCode extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        // Access the editor and project
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        Project project = anActionEvent.getProject();
        if (editor == null || project == null) return;

        // Get the PsiFile corresponding to the current editor
        PsiFile currentFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (currentFile == null) return;

        pasteClonedElements(editor, project, currentFile);
    }

    private void pasteClonedElements(Editor editor, Project project, PsiFile currentFile) {
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement pasteTarget = currentFile.findElementAt(caretOffset);
        if (pasteTarget == null) return;

        // Get the list of elements to paste
        List<PsiElement> elementsToPaste = CloneAssetCode.elementsInRange;
        if (elementsToPaste == null || elementsToPaste.isEmpty()) return;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            for (PsiElement element : elementsToPaste) {
                // Clone the element and add it to the target location
                PsiElement clonedElement = element.copy();
                pasteTarget.getParent().addBefore(clonedElement, pasteTarget);
            }
        });
    }
}
