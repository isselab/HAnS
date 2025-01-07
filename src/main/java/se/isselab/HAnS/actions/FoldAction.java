package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class FoldAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Get the editor
        Editor editor = e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR);
        Project project = e.getProject();

        if (editor == null || project == null) {
            Messages.showErrorDialog("No editor is currently open.", "Error");
            return;
        }

        // Use FoldingModel to add a folding region
        FoldingModel foldingModel = editor.getFoldingModel();
        foldingModel.runBatchFoldingOperation(() -> {
            // Define the range of text to fold
            int startOffset = editor.getDocument().getLineStartOffset(1);
            int endOffset = editor.getDocument().getLineEndOffset(10);

            // Add a folding region
            foldingModel.addFoldRegion(startOffset, endOffset, "...folded code...")
                    .setExpanded(true); // Collapse by default
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        // Enable the action only if an editor is open
        e.getPresentation().setEnabled(e.getData(com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR) != null);
    }
}