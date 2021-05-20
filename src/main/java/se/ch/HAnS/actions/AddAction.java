package se.ch.HAnS.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;

public class AddAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getData(LangDataKeys.PSI_ELEMENT) instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) e.getData(LangDataKeys.PSI_ELEMENT);
            if (feature != null) {
                feature.addFeature();
            }
        }
    }
}
