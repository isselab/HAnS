package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.states.ToggleStateService;
import com.intellij.openapi.diagnostic.Logger;

public class ToggleAction extends com.intellij.openapi.actionSystem.ToggleAction {
    private static final Logger LOG = Logger.getInstance(ToggleAction.class);

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return false; // Handle the case where the project is null.
        }

        ToggleStateService service = project.getService(ToggleStateService.class);
        return service.getState().isEnabled;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean b) {
        Project project = e.getProject();
        if (project == null) {
            return; // Handle the case where the project is null.
        }

        ToggleStateService service = project.getService(ToggleStateService.class);
        LOG.info("ToggleAction executed. Annotations " + (b ? "enabled" : "disabled"));
        service.getState().isEnabled = b;
    }


    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setText(isSelected(e) ? "Disable Annotations" : "Enable Annotations");
    }
}