package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureExtension.BackgroundTask;
import se.isselab.HAnS.featureExtension.FeatureService;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureTangling;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.HashMap;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        FeatureService featureService = new FeatureService();
        AtomicReference<HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>>> tanglingMap = new AtomicReference<>(new HashMap<>());

        System.out.println("starting background");

        // BackgroundTask task = new BackgroundTask(e.getProject(), "Scanning features", new CallbackClass());


        //ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());

       ProgressManager.getInstance().run(new Task.Modal(e.getProject(), "Processing Features", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    long start = System.currentTimeMillis();
                    if(!indicator.isRunning())
                        indicator.start();
                    var featureList = FeatureModelUtil.findFeatures(e.getProject());
                    int counter = 0;
                    indicator.setText("Seraching for References");
                    /*
                    for (var feature : featureList) {
                        indicator.setText2("scanning: " + feature.getLPQText());

                        FeatureLocationManager.getFeatureFileMapping(feature);

                        counter++;
                        double fraction = (double) featureList.size() / (double) counter;
                        indicator.setFraction(fraction);
                    }
                    indicator.setText("Calculating Tangling metrics");
                    indicator.setText2("");
                    */
                    tanglingMap.set(featureService.getTanglingMap());
                    long end = System.currentTimeMillis();
                    System.out.println("took: " + (end - start) + "ms");
                });
            }
        });
        HashMap<String, FeatureFileMapping> mapping = new HashMap<>();
        for(var feature : FeatureModelUtil.findFeatures(e.getProject())){
            mapping.put(feature.getLPQText(), FeatureLocationManager.getFeatureFileMapping(feature));
        }
        System.out.println("Tree & TreeMap:\n" + FeatureTangling.getFeatureJSON(FeatureTangling.Mode.Tree));
        System.out.println("Tangling:\n" + FeatureTangling.getFeatureJSON(FeatureTangling.Mode.Tangling));

       // System.out.println("Map: " + tanglingMap);

        //TODO THESIS:
        // how to get project

        /*
        for(FeatureModelFeature feature : FeatureModelUtil.findFeatures(e.getProject())){
            feature.getReferences();
            FeatureFileMapping featureFileMapping = FeatureLocationManager.getFeatureFileMapping(feature);
            System.out.println("Feature: " + feature.getName());

            /*
            if(feature.getParent() instanceof FeatureModelFile){
                System.out.println("Parent: " + ((FeatureModelFile)feature.getParent()).getName());
            }
            else {
                System.out.println("Parent: " + ((FeatureModelFeatureImpl) feature.getParent()).getName());
            }
            for(var child : feature.getChildren()){
                System.out.println("Child: " + ((FeatureModelFeatureImpl) child).getName());
            }




            for(String file : featureFileMapping.getAllFeatureLocations().keySet()){
                var annotationToBlockPair = featureFileMapping.getAllFeatureLocations().get(file);
                System.out.println("  File: " + file + "\n  Type: " + annotationToBlockPair.first.toString());


                for(FeatureLocationBlock featureLocationBlock : annotationToBlockPair.second){
                    System.out.println("    " + featureLocationBlock.toString());
                }
                System.out.println("  Total lines: [" + featureFileMapping.getFeatureLineCountInFile(file) + "]");
            }
            System.out.println("/////\n");


        }

        System.out.println("Done with action performed");
        */

    }

}
