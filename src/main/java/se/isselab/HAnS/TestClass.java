package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;

import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.singleton.HAnSManager;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        HAnSManager singleton = HAnSManager.getInstance();
        for(FeatureModelFeature feature : FeatureModelUtil.findFeatures(singleton.getProject())){
            FeatureFileMapping featureFileMapping = FeatureLocationManager.getFeatureFileMapping(feature);
            System.out.println("Feature: " + feature.getName());
            System.out.println("Test");

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
                System.out.println("  File: " + file);

                for(FeatureLocationBlock featureLocationBlock : featureFileMapping.getAllFeatureLocations().get(file)){
                    System.out.println("    " + featureLocationBlock.toString());
                }

            }
        }
        //startBackgroundTask(singleton);
    }

    /**
     * Starts Background Task for FeatureLocationManager.
     *
     * @param singleton
     * @see se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask
     */
    private void startBackgroundTask(HAnSManager singleton) {
        FeatureLocationBackgroundTask backgroundTask = new FeatureLocationBackgroundTask(singleton.getProject(),
                "Scanning progress",
                true,
                PerformInBackgroundOption.DEAF);
        // TODO THESIS: singleton of ProgressIndicator
        ProgressIndicator empty = new EmptyProgressIndicator();

        ProgressManager progressManager = ProgressManager.getInstance();
        Logger.print("start background task");
        progressManager.runProcessWithProgressAsynchronously(backgroundTask, empty);
    }
}
