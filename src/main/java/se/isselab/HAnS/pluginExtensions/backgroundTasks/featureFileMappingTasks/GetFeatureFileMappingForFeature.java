package se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

public class GetFeatureFileMappingForFeature extends Task.Backgroundable {

    private FeatureFileMapping featureFileMapping;
    private final FeatureModelFeature feature;
    private final FeatureFileMappingCallback callback;

    public GetFeatureFileMappingForFeature(@Nullable Project project, @NotNull String title, FeatureFileMappingCallback callback, FeatureModelFeature feature) {
        super(project, title);
        this.callback = callback;
        this.feature = feature;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        featureFileMapping = FeatureLocationManager.getFeatureFileMapping(super.getProject(), feature);
    }

    @Override
    public void onSuccess() {
        callback.onComplete(featureFileMapping);
    }
}
