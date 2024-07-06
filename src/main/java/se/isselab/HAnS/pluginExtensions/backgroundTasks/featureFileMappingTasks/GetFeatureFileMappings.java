package se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;

import java.util.Map;

public class GetFeatureFileMappings extends Task.Backgroundable{

    private Map<String, FeatureFileMapping> featureFileMappings;
    private final FeatureFileMappingCallback callback;

    public GetFeatureFileMappings(@Nullable Project project, @NotNull String title, FeatureFileMappingCallback callback) {
        super(project, title);
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
    }

    @Override
    public void onSuccess() {
        callback.onComplete(featureFileMappings);
    }
}
