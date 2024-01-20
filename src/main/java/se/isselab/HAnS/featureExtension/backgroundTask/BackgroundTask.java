package se.isselab.HAnS.featureExtension.backgroundTask;


import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.HAnSCallback;
import se.isselab.HAnS.metrics.FeatureMetrics;

public abstract class BackgroundTask extends Task.Backgroundable{

    HAnSCallback callback;

    FeatureMetrics featureMetrics;
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     * @param project current Project
     * @param title Title for Progress Indicator
     * @param callback  {@link HAnSCallback} Implementation
     */
    public BackgroundTask(@Nullable Project project, @NotNull String title, HAnSCallback callback, FeatureMetrics featureMetrics) {
        super(project, title);
        this.callback = callback;
        this.featureMetrics = featureMetrics;
    }

    @Override
    public void onSuccess() {
        System.out.println("Starting on success");
        callback.onComplete(featureMetrics);
    }
}
