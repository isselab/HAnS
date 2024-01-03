package se.isselab.HAnS;


import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
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

        if(!DumbService.isDumb(ProjectManager.getInstance().getOpenProjects()[0])) {
            var featureList = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));

            for (var feature : featureList) {

                indicator.setText2("scanning: " + ReadAction.compute(feature::getLPQText));

                // indicator.setText2("scanning: " + feature.getLPQText());
                // FeatureFileMapping featureFileMapping = FeatureLocationManager.getFeatureFileMapping(feature);
                featureFileMappings.add(ReadAction.compute(() -> FeatureLocationManager.getFeatureFileMapping(feature)));
            }

            System.out.println("done with background");
        }
        else {
            System.out.println("still indexing");
        }
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
