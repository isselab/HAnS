package se.ch.HAnS.featureModel.toolWindow.actions;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.toolWindow.FeatureView;

public class MyTypedHandler extends TypedHandlerDelegate {
    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (file.getVirtualFile().getExtension().equals("feature-model")) {
            ApplicationManager.getApplication().invokeLater(FeatureView::clear);
        }
        return Result.STOP;
    }
}