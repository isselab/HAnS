package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        FeatureService featureService = new FeatureService();

        //TODO THESIS:
        // how to get project
        for(FeatureModelFeature feature : FeatureModelUtil.findFeatures(e.getProject())){
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
            */

            var tanglingDegree = featureService.getFeatureTangling(feature);
            System.out.println(" Tangling: " + tanglingDegree);

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
    }


}
