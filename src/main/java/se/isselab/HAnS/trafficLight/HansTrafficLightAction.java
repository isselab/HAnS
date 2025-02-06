package se.isselab.HAnS.trafficLight;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class HansTrafficLightAction extends AnAction implements DumbAware, CustomComponentAction {
    private Editor editor;
    HansTrafficLightAction() {
        super();
    }
    HansTrafficLightAction(Editor editor) {
        this.editor = editor;
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return new HansTrafficLightWidget(this, presentation, place, editor);
    }

    @Override
    public void updateCustomComponent(@NotNull JComponent component, @NotNull Presentation presentation) {
        CustomComponentAction.super.updateCustomComponent(component, presentation);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public final void update(AnActionEvent e) {
        var p = e.getProject();
        if (p == null || !p.isInitialized() || p.isDisposed()) {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        var visible = e.getPresentation().isVisible();
        e.getPresentation().setVisible(visible);
        if (!visible) {
            e.getPresentation().setEnabled(false);
            return;
        }

        e.getPresentation().setEnabled(true);
    }
}
