package se.isselab.HAnS.metrics;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import se.isselab.HAnS.Logger;
import se.isselab.HAnS.featureExtension.FeatureService;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class FeatureTangling {

    /**
     * Returns the tangling degree of the given feature
     * @param feature the feature which should be checked
     * @return tangling degree of the given feature
     */
    public static int getFeatureTanglingDegree(FeatureModelFeature feature){
        FeatureService featureService = new FeatureService();
        return getFeatureTanglingDegree(featureService.getAllFeatureFileMappings(), feature);
    }

    /**
     * Returns the tangling degree of the given feature while making use of a precalculated fileMapping
     * @param fileMappings pre calculated fileMapping
     * @param feature the feature which should be checked
     * @return tangling degree of the given feature
     */
    public static int getFeatureTanglingDegree(HashMap<String, FeatureFileMapping> fileMappings, FeatureModelFeature feature){
        var tanglingMap = getTanglingMap(fileMappings);

        if(tanglingMap.containsKey(feature))
            return tanglingMap.get(feature).size();

        return 0;
    }

    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features
     * @return TanglingMap
     */
    public static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(){
        FeatureService featureService = new FeatureService();
       return getTanglingMap(featureService.getAllFeatureFileMappings());
    }
    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features while making use of a precalculated fileMapping
     * @param fileMappings
     * @return TanglingMap
     */
    public static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(HashMap<String, FeatureFileMapping> fileMappings){
        //TODO THESIS
        // maybe return a class container instead of a map to make access more convenient

        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        //map which contains Features and their tangled features
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = new HashMap<>();

        //map which contains file to {features and their blocks}
        HashMap<String, HashMap<FeatureModelFeature, ArrayList<FeatureLocationBlock>>> featureFileMapping = new HashMap<>();
        //iterate over each feature and get the locations from them
        for(FeatureModelFeature feature : ReadAction.compute(()->FeatureModelUtil.findFeatures(project))) {
            //get information for the corresponding feature
            var locationMap = fileMappings.get(feature.getLPQText());

            //create entry for the featureFileMapping - this entry contains the feature and the feature locations within the file specified by filePath

            //iterate over each file inside this map
            for (var fileMapping : locationMap.getAllFeatureLocations().entrySet()){
                //get the path and the corresponding feature locations within this path
                String filePath = fileMapping.getKey();
                ArrayList<FeatureLocationBlock> locations = fileMapping.getValue().second;

                //add the {feature to location[]} to the fileMap
                var map = featureFileMapping.get(filePath);
                if(map != null){
                    //the file is already associated with features - check for tangling
                    for(var existingFeatureLocations : map.entrySet()){
                        //iterate over the locations of the feature and check if any of them do intersect
                        for(FeatureLocationBlock block : locations){
                            if(block.hasSharedLines(existingFeatureLocations.getValue().toArray(new FeatureLocationBlock[0]))){
                                //features share the same lines of code

                                //add tangling entry for both features a->b and b->a
                                var featureB = existingFeatureLocations.getKey();

                                //add featureB to featureA
                                if(tanglingMap.containsKey(feature)){
                                    tanglingMap.get(feature).add(featureB);
                                }
                                else{
                                    HashSet<FeatureModelFeature> featureSet = new HashSet<>();
                                    featureSet.add(featureB);
                                    tanglingMap.put(feature, featureSet);
                                }

                                //add featureA to featureB
                                if(tanglingMap.containsKey(featureB)){
                                    tanglingMap.get(featureB).add(feature);
                                }
                                else{
                                    HashSet<FeatureModelFeature> featureSet = new HashSet<>();
                                    featureSet.add(feature);
                                    tanglingMap.put(featureB, featureSet);
                                }
                            }
                        }
                    }
                    //add feature to the map
                    map.put(feature, locations);

                }
                else{
                    //the file is new so we add a new entry
                    HashMap<FeatureModelFeature, ArrayList<FeatureLocationBlock>> featureLocationMap = new HashMap<>();
                    featureLocationMap.put(feature, locations);
                    featureFileMapping.put(filePath, featureLocationMap);
                }
            }
        }

        return tanglingMap;
    }

}
