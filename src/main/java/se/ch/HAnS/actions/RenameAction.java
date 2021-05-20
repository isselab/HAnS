package se.ch.HAnS.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.refactoring.rename.RenameDialog;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Objects;

public class RenameAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getData(LangDataKeys.PSI_ELEMENT) instanceof FeatureModelFeature) {
            RenameDialog dialog = new RenameDialog(Objects.requireNonNull(e.getProject()), Objects.requireNonNull(e.getData(LangDataKeys.PSI_ELEMENT)), null, null);
            dialog.show();
        }
    }
}
