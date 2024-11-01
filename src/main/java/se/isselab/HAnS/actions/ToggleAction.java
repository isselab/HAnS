package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.states.ToggleStateService;

public class ToggleAction extends com.intellij.openapi.actionSystem.ToggleAction {
    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
        ToggleStateService service = ToggleStateService.getInstance();
        return service != null && service.getState().isEnabled;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
        ToggleStateService service = ToggleStateService.getInstance();
        if (service != null) {
            service.getState().isEnabled = b;
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(isSelected(e) ? "Disable Annotations" : "Enable Annotations");
        // Eventually change looks of the button
    }
}