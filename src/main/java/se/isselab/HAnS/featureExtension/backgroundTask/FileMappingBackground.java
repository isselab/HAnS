package se.isselab.HAnS.featureExtension.backgroundTask;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.HAnSCallback;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureMetrics;

public class FileMappingBackground extends BackgroundTask {
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     *
     * @param project
     * @param title
     * @param callback
     * @param featureMetrics
     */
    public FileMappingBackground(@Nullable Project project, @NotNull String title, HAnSCallback callback, FeatureMetrics featureMetrics) {
        super(project, title, callback, featureMetrics);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        FeatureModelFeature feature = featureMetrics.getFeature();
        if(feature==null){
            featureMetrics = new FeatureMetrics();
        }
        else {
            featureMetrics = new FeatureMetrics(FeatureLocationManager.getFeatureFileMapping(super.getProject(), feature));
        }
    }
}
