package se.isselab.HAnS;


import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;

import java.util.ArrayList;

public class BackgroundTask extends Task.Backgroundable{

    HAnSCallback callback;
    ArrayList<FeatureFileMapping> featureFileMappings = new ArrayList<>();
    public BackgroundTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, HAnSCallback callback) {
        super(project, title);
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        var featureList = FeatureModelUtil.findFeatures(getProject());

        for (var feature : featureList) {
            indicator.setText2("scanning: " + feature.getLPQText());
            featureFileMappings.add(FeatureLocationManager.getFeatureFileMapping(feature));
        }

        System.out.println("done with background");
    }



    @Override
    public void onCancel() {
        super.onCancel();
    }

    @Override
    public void onSuccess() {
        System.out.println("Starting on success");
        callback.onComplete(featureFileMappings);

    }

    @Override
    public void onFinished() {
        super.onFinished();
    }
}
