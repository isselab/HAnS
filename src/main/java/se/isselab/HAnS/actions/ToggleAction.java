package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.states.ToggleStateService;
import com.intellij.openapi.diagnostic.Logger;

public class ToggleAction extends com.intellij.openapi.actionSystem.ToggleAction {
    private static final Logger LOG = Logger.getInstance(ToggleAction.class);

    @Override
    // determines whether the toggle is currently enabled or not
    public boolean isSelected(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return false; // Safeguard against null projects
        }

        ToggleStateService service = ToggleStateService.getInstance(project);
        if (service == null) {
            return false; // Handle service retrieval failure
        }

        return service.isEnabled(); // Use the new `isEnabled()` method
    }

    @Override
    // toggles the state
    public void setSelected(@NotNull AnActionEvent e, boolean b) {
        Project project = e.getProject();
        if (project == null) {
            return; // Safeguard against null projects
        }

        ToggleStateService service = ToggleStateService.getInstance(project);
        LOG.info("ToggleStateService state: " + (service != null ? service.isEnabled() : "Service is null")); // retrieves toggle state and logs its current state or logs if service is unavailable
        if (service != null) {
            service.setEnabled(b, project); // Use the new `setEnabled()` method
            LOG.info("ToggleAction executed. Annotations " + (b ? "enabled" : "disabled"));
        } else {
            LOG.warn("Failed to retrieve ToggleStateService.");
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
        e.getPresentation().setText(isSelected(e) ? "Disable Annotations" : "Enable Annotations");
    }

}
