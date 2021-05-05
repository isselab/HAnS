package se.ch.HAnS.featureModel.toolWindow.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.toolWindow.FeatureView;

public class AddAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FeatureView.getView().addFeature();
    }
}
