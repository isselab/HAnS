package se.isselab.HAnS.featureExtension.v2;

import com.intellij.openapi.components.Service;

import com.intellij.openapi.project.Project;

import se.isselab.HAnS.featureExtension.v2.backgroundTasks.ProjectMetricsBackgroundTask;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;

@Service(Service.Level.PROJECT)
public final class ProjectMetricsService implements MetricsService {

    private final Project project;

    public ProjectMetricsService(Project project){
        this.project = project;
    }

    @Override
    public void getProjectMetricsBackground(MetricsCallback callback) {
        ProjectMetricsBackgroundTask task = new ProjectMetricsBackgroundTask(project, "Refreshing metrics...", callback, null);
        task.queue();
    }

    @Override
    public boolean isRootFeature(FeatureModelFeature feature) {
        return feature.getParent() instanceof FeatureModelFile;
    }
}
