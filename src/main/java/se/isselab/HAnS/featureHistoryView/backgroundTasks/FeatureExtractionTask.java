package se.isselab.HAnS.featureHistoryView.backgroundTasks;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import java.util.List;
import java.util.stream.Collectors;

public class FeatureExtractionTask extends Task.Backgroundable {
    private final Project project;
    private final FeatureExtractionCallback callback;
    List<String> features;

    public FeatureExtractionTask(Project project, FeatureExtractionCallback callback) {
        super(project, "Extracting features for feature timeline");
        this.project = project;
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(project)
                .stream()
                .map(feature -> feature.getLPQText())
                .collect(Collectors.toList()));
    }

    @Override
    public void onSuccess() {
        callback.onFeaturesExtracted(features);
    }
}

