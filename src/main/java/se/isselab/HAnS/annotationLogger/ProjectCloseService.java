package se.isselab.HAnS.annotationLogger;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

/**
 * This class handles the project close service. It is responsible for the initiation and management of the
 * ProjectCloseListener instance for a project.
 */
public class ProjectCloseService {
    private final Project project;
    private ProjectCloseListener projectCloseListener;

    /**
     * Constructor to initialize the service.
     *
     * @param project The project for which the service is instantiated.
     */
    public ProjectCloseService(Project project) {
        this.project = project;
    }

    /**
     * Returns the instance of ProjectCloseService associated with a given project.
     *
     * @param project The project for which to get the service instance.
     * @return The instance of ProjectCloseService for the given project.
     */
    public static ProjectCloseService getInstance(@NotNull Project project) {
        return project.getService(ProjectCloseService.class);
    }

    /**
     * Initializes a new ProjectCloseListener and subscribes it to the project's message bus.
     *
     * @param customDocumentListener The CustomDocumentListener object for the project.
     */
    public void init(CustomDocumentListener customDocumentListener) {
        projectCloseListener = new ProjectCloseListener(project, customDocumentListener);
        project.getMessageBus().connect().subscribe(ProjectManager.TOPIC, projectCloseListener);
    }

    /**
     * Returns the current ProjectCloseListener object.
     *
     * @return The current ProjectCloseListener object.
     */
    public ProjectCloseListener getProjectCloseListener() {
        return projectCloseListener;
    }

    /**
     * Updates the ProjectCloseListener with a new CustomDocumentListener object.
     *
     * @param customDocumentListener The new CustomDocumentListener object to set.
     */
    public void updateProjectCloseListener(CustomDocumentListener customDocumentListener) {
        if (projectCloseListener != null) {
            projectCloseListener.setCustomDocumentListener(customDocumentListener);
        }
    }

    /**
     * Returns the current CustomDocumentListener object.
     *
     * @return The current CustomDocumentListener object.
     */
    public CustomDocumentListener getCurrentCustomDocumentListener() {
        return projectCloseListener.getCustomDocumentListener();
    }
}
