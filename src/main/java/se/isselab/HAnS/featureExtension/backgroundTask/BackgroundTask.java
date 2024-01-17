package se.isselab.HAnS.featureExtension.backgroundTask;


import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.HAnSCallback;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public abstract class BackgroundTask extends Task.Backgroundable{

    HAnSCallback callback;

    FeatureMetrics featureMetrics;
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     * @param project
     * @param title
     * @param callback
     */
    public BackgroundTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title, HAnSCallback callback, FeatureMetrics featureMetrics) {
        super(project, title);
        this.callback = callback;
        this.featureMetrics = featureMetrics;
    }
/*

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        */
/*FeatureService featureService = new FeatureService();*//*

        HashMap<String, FeatureFileMapping> featureFileMappings = null;
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = null;
        HashMap<String, FeatureFileMapping> fileMapping = null;
        FeatureModelFeature feature = null;
        int tanglingDegree = -1;
        int scatteringDegree = -1;

        // TODO THESIS: Scattering
        */
/*if((options & Mode.FEATUREFILEMAPPINGS) > 0 && (options & Mode.TANGLINGMAP) > 0) {
            // 0011
            System.out.println("was file and tangling request");
            featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings();
            tanglingMap = featureService.getTanglingMap(featureFileMappings);

        }
        else {
            if((options & Mode.FEATUREFILEMAPPINGS) > 0){
                System.out.println("was file request");
                // 0001
                featureFileMappings = featureService.getAllFeatureFileMappings();
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
        // TODO THESIS: Scattering*//*

        featureMetrics = new FeatureMetrics(featureFileMappings, tanglingMap);
    }

*/

    @Override
    public void onSuccess() {
        System.out.println("Starting on success");
        callback.onComplete(featureMetrics);
    }
}
