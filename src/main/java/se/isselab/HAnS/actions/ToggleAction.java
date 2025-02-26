package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.states.ToggleStateService;
import com.intellij.openapi.diagnostic.Logger;

public class ToggleAction extends com.intellij.openapi.actionSystem.ToggleAction {
    private static final Logger LOG = Logger.getInstance(ToggleAction.class);


    // determines whether the toggle is currently enabled or not
    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        //LOG.debug("isSelected called");
        Project project = e.getProject();
        if (project == null) {
            //LOG.warn("isSelected: No project found in AnActionEvent");
            return false; // Safeguard against null projects
        }

        ToggleStateService service = ToggleStateService.getInstance(project);
        if (service == null) {
            //LOG.warn("isSelected: ToggleStateService is null");
            return false;
        }

        boolean enabled = service.isEnabled();
        //LOG.debug("isSelected: ToggleStateService.isEnabled() = " + enabled);
        return enabled; // Use the new `isEnabled()` method
    }



    // toggles the state
    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean b) {
        //LOG.debug("setSelected called with value: " + b);
        Project project = e.getProject();
        if (project == null) {
            //LOG.warn("setSelected: No project found in AnActionEvent");
            return; // Safeguard against null projects
        }

        ToggleStateService service = ToggleStateService.getInstance(project);
        //LOG.info("setSelected: ToggleStateService state before setting: " + (service != null ? service.isEnabled() : "Service is null"));

        if (service != null) {
            service.setEnabled(b, project); // Use the new `setEnabled()` method
            //LOG.info("setSelected: ToggleAction executed. Annotations " + (b ? "enabled" : "disabled"));
        } else {
            //LOG.warn("setSelected: Failed to retrieve ToggleStateService.");
        }
    }


@Override
public void update(@NotNull AnActionEvent e) {
    boolean isCurrentlySelected = isSelected(e);
    //LOG.debug("update called: current selection = " + isCurrentlySelected);
    e.getPresentation().setEnabledAndVisible(true);
    e.getPresentation().setText(isCurrentlySelected ? "Disable Annotations" : "Enable Annotations");
    //LOG.debug("update: Presentation text set to " + (isCurrentlySelected ? "Disable Annotations" : "Enable Annotations"));
}


}
