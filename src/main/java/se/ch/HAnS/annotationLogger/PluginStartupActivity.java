package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {
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
