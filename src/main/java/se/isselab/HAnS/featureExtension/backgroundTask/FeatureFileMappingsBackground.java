package se.isselab.HAnS.featureExtension.backgroundTask;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.HAnSCallback;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.metrics.FeatureMetrics;


public class FeatureFileMappingsBackground extends BackgroundTask{
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     * @param project current Project
     * @param title Title for Progress Indicator
     * @param callback  {@link HAnSCallback} Implementation
     */
    public FeatureFileMappingsBackground(@Nullable Project project, @NotNull String title, HAnSCallback callback, FeatureMetrics featureMetrics) {
        super(project, title, callback, featureMetrics);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        featureMetrics = new FeatureMetrics(FeatureLocationManager.getAllFeatureFileMappings(super.getProject()), null);
    }
}
