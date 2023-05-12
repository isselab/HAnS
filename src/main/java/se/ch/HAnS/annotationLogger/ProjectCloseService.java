package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

public class ProjectCloseService {
    private final Project project;
    private ProjectCloseListener projectCloseListener;

    public ProjectCloseService(Project project) {
        this.project = project;
    }

    public static ProjectCloseService getInstance(@NotNull Project project) {
        return project.getService(ProjectCloseService.class);
    }

    public void init(CustomDocumentListener customDocumentListener) {
        projectCloseListener = new ProjectCloseListener(project, customDocumentListener);
        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, projectCloseListener);
    }
    public ProjectCloseListener getProjectCloseListener() {
        return projectCloseListener;
    }

    public void updateProjectCloseListener(CustomDocumentListener customDocumentListener) {
        if (projectCloseListener != null) {
            projectCloseListener.setCustomDocumentListener(customDocumentListener);
        }
    }
    public CustomDocumentListener getCurrentCustomDocumentListener() {
        return projectCloseListener.getCustomDocumentListener();
    }
}
