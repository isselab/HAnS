package se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;

import java.util.HashSet;

public class GetTangledFeaturesForFeature extends Task.Backgroundable{

    private HashSet<FeatureModelFeature> tangledFeatures;
    private final FeatureModelFeature feature;
    private final TanglingMapCallback callback;

    public GetTangledFeaturesForFeature(@Nullable Project project, @NotNull String title, TanglingMapCallback callback, FeatureModelFeature feature) {
        super(project, title);
        this.callback = callback;
        this.feature = feature;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        var featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
        var tanglingMap = FeatureTangling.getTanglingMap(super.getProject(), featureFileMappings);
        tangledFeatures = tanglingMap.get(feature);
    }

    @Override
    public void onSuccess() {
        callback.onComplete(tangledFeatures);
    }
}
