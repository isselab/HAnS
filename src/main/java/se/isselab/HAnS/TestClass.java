package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureExtension.FeatureService;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashMap;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        FeatureService featureService = new FeatureService();
        var tanglingMap = featureService.getAllFeatureTangling();

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
            }*/


            var tanglingDegree = tanglingMap.get(feature) != null ? tanglingMap.get(feature).size() : 0;
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
        System.out.println("Done");

    }

    /**
     * Helperfunction to get TanglingMapJSON  - should be put into HAnS-Vis later on
     * @return JSON of tangling map which is compatible with echarts
     */
    public JSONObject getTanglingMapJSON(){
        //converts TanglingMap to JSON

        FeatureService featureService = new FeatureService();
        var tanglingMap = featureService.getAllFeatureTangling();
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        JSONObject dataJSON = new JSONObject();
        JSONArray nodesJSON = new JSONArray();
        JSONArray linksJSON = new JSONArray();

        //get links
        //map feature with id
        HashMap<FeatureModelFeature, Integer> featureToId = new HashMap<>();
        int counter = 0;
        for(var feature : FeatureModelUtil.findFeatures(project)){
            JSONObject obj = new JSONObject();
            obj.put("id", feature.getLPQText());
            obj.put("name", feature.getLPQText());
            var tangledFeatureMap = tanglingMap.get(feature);
            int tanglingDegree = tangledFeatureMap != null ? tangledFeatureMap.size() : 0;
            obj.put("symbolSize", 20 + 20 * tanglingDegree);

            obj.put("value", tanglingDegree);
            nodesJSON.add(obj);
            featureToId.put(feature, counter);
            counter++;
        }

        for(var featureToTangledFeatures : tanglingMap.entrySet()){
            for(var tangledFeature : featureToTangledFeatures.getValue()){
                //add link if id of feature is less than the id of the tangled one
                if(featureToId.get(featureToTangledFeatures.getKey()) < featureToId.get(tangledFeature))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("source", featureToTangledFeatures.getKey().getLPQText());
                    obj.put("target", tangledFeature.getLPQText());
                    linksJSON.add(obj);
                }
            }
        }
        dataJSON.put("nodes", nodesJSON);
        dataJSON.put("links", linksJSON);

        return dataJSON;
    }


}
