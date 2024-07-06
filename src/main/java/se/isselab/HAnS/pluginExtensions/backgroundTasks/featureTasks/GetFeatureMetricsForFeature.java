package se.isselab.HAnS.pluginExtensions.backgroundTasks.featureTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

public class GetFeatureMetricsForFeature extends Task.Backgroundable{

    private final FeatureModelFeature feature;
    private final FeatureCallback callback;

    public GetFeatureMetricsForFeature(@Nullable Project project, @NotNull String title, FeatureCallback callback, FeatureModelFeature feature) {
        super(project, title);
        this.callback = callback;
        this.feature = feature;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        // Start the progress indicator
        indicator.setIndeterminate(false);
        indicator.setText("Getting metrics for feature");
        indicator.setFraction(0.0);

        new GetScatteringDegreeForFeature(super.getProject(), "GenerateScatteringDegree", callback, feature).run(indicator);
        indicator.setFraction(0.25);
        new GetTanglingDegreeForFeature(super.getProject(), "GenerateTanglingDegree", callback, feature).run(indicator);
        indicator.setFraction(0.5);
        new GetNestingDepthsForFeature(super.getProject(), "GenerateNestingDepths", callback, feature).run(indicator);
        indicator.setFraction(0.75);
        new GetNumberOfAnnotationsForFeature(super.getProject(), "GenerateNumberOfAnnotatedFiles", callback, feature).run(indicator);
        indicator.setFraction(1.0);
        indicator.setText("Metrics for feature loaded");
    }

    @Override
    public void onSuccess() {
        callback.onComplete(feature);
    }
}
