package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the plugin start-up activities whenever a project is opened.
 * It extends StartupActivity from IntelliJ SDK, which is an interface that allows activities
 * to be performed on project start-up.
 */
public class PluginStartupActivity implements StartupActivity {
    /**
     * This is the main method of the class that is invoked whenever a project is opened.
     * It sets up the customDocumentListeners for the opened project and initiates the project close service.
     * It skips this setup for a project named "HAnS", it does this to avoid duplicate project being opened.
     *
     * @param project The project that has been opened.
     */
    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (project.getName().equalsIgnoreCase("HAnS")) {
                System.out.println("Skipping PluginStartupActivity for HAnS project");
                return;
            }
            System.out.println("PluginStartupActivity invoked for: " + project.getName());
            CustomDocumentListener customDocumentListener = CustomDocumentListener.getInstance(project);
            ProjectCloseService projectCloseService = ProjectCloseService.getInstance(project);

            ProjectCloseListener projectCloseListener = projectCloseService.getProjectCloseListener();
            if (projectCloseListener != null) {
                CustomDocumentListener oldCustomDocumentListener = projectCloseService.getCurrentCustomDocumentListener();
                System.out.println("Old CustomDocumentListener Instance: " + oldCustomDocumentListener.hashCode());
                System.out.println("New CustomDocumentListener Instance: " + customDocumentListener.hashCode());
                projectCloseListener.setCustomDocumentListener(customDocumentListener);
            } else {
                projectCloseService.init(customDocumentListener);
            }
        });
    }
}
