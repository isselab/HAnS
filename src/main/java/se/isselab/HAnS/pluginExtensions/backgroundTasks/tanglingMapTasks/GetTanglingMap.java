package se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;

import java.util.HashMap;
import java.util.HashSet;

public class GetTanglingMap extends Task.Backgroundable{

    private HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap;
    private final TanglingMapCallback callback;

    public GetTanglingMap(@Nullable Project project, @NotNull String title, TanglingMapCallback callback) {
        super(project, title);
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        var featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
        tanglingMap = FeatureTangling.getTanglingMap(super.getProject(), featureFileMappings);
    }

    @Override
    public void onSuccess() {
        callback.onComplete(tanglingMap);
    }
}
