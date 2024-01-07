package se.isselab.HAnS.featureExtension;


import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.HAnSCallback;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureMetrics;
import se.isselab.HAnS.metrics.FeatureTangling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BackgroundTask extends Task.Backgroundable{

    HAnSCallback callback;
    int options;
    ArrayList<FeatureFileMapping> featureFileMappings = new ArrayList<>();

    FeatureMetrics featureMetrics;
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     * @param project
     * @param title
     * @param callback
     */
    public BackgroundTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, HAnSCallback callback, int options) {
        super(project, title);
        this.callback = callback;
        this.options = options;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        FeatureService featureService = new FeatureService();
        HashMap<String, FeatureFileMapping> fileMapping = null;
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = null;
        // TODO THESIS: Scattering
        if((options & Mode.FILEMAPPING) > 0 && (options & Mode.TANGLINGMAP) > 0) {
            // 0011
            System.out.println("was file and tangling request");
            fileMapping = featureService.getAllFeatureFileMappings();
            tanglingMap = featureService.getTanglingMap(fileMapping);

        }
        else {
            if((options & Mode.FILEMAPPING) > 0){
                System.out.println("was file request");
                // 0001
                fileMapping = featureService.getAllFeatureFileMappings();
            }
            if((options & Mode.TANGLINGMAP) > 0){
                System.out.println("was tangling request");
                // 0010
                tanglingMap = featureService.getTanglingMap();
            }
        }
        if((options & Mode.SCATTERING) > 0){                            // 0100
            // TODO THESIS: Scattering
        }
        // TODO THESIS: Scattering
        featureMetrics = new FeatureMetrics(fileMapping, tanglingMap);

        /*if(!DumbService.isDumb(ProjectManager.getInstance().getOpenProjects()[0])) {
            var featureList = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));

            for (var feature : featureList) {

                indicator.setText2("scanning: " + ReadAction.compute(feature::getLPQText));

                // indicator.setText2("scanning: " + feature.getLPQText());
                // FeatureFileMapping featureFileMapping = FeatureLocationManager.getFeatureFileMapping(feature);
                featureFileMappings.add(ReadAction.compute(() -> FeatureLocationManager.getFeatureFileMapping(feature)));
            }

            System.out.println("done with background");
        }*/
        /*else {
            System.out.println("still indexing");
        }*/
    }



    @Override
    public void onCancel() {
        super.onCancel();
    }

    @Override
    public void onSuccess() {
        System.out.println("Starting on success");
        callback.onComplete(featureMetrics);
    }

    @Override
    public void onFinished() {
        super.onFinished();
    }
}
