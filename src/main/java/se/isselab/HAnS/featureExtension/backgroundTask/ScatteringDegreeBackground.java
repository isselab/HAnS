package se.isselab.HAnS.featureExtension.backgroundTask;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.HAnSCallback;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureMetrics;
import se.isselab.HAnS.metrics.FeatureScattering;

public class ScatteringDegreeBackground extends BackgroundTask {
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     *
     * @param project  current Project
     * @param title    Title for Progress Indicator
     * @param callback {@link HAnSCallback} Implementation
     */
    public ScatteringDegreeBackground(@Nullable Project project, @NotNull String title, HAnSCallback callback, FeatureMetrics featureMetrics) {
        super(project, title, callback, featureMetrics);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        FeatureModelFeature feature = featureMetrics.getFeature();
        int scatteringDegree = FeatureScattering.getScatteringDegree(super.getProject(), feature);
        featureMetrics = new FeatureMetrics(feature, -1, scatteringDegree);
    }
}
