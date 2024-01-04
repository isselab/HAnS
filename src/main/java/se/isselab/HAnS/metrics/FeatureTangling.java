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
import java.util.List;

public class FeatureTangling {

    public enum Mode {Default, Tree, TreeMap, Tangling}


    private static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMapCache = null;
    private static HashMap<String, FeatureFileMapping> fileMappingCache = null;

    /**
     * Returns the tangling degree of the given feature
     * @param feature the feature which should be checked
     * @return tangling degree of the given feature
     */
    public static int getFeatureTanglingDegree(FeatureModelFeature feature){
        var tanglingMap = getTanglingMap();

        if(tanglingMap.containsKey(feature))
            return tanglingMap.get(feature).size();

        //TODO THESIS
        // errorhandling
        return 0;
    }

    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features
     * @return TanglingMap
     */
    public static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(){
        //TODO THESIS
        // maybe return a class container instead of a map to make access more convenient

        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        FeatureService featureService = new FeatureService();

        //map which contains Features and their tangled features
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = new HashMap<>();

        //map which contains file to {features and their blocks}
        HashMap<String, HashMap<FeatureModelFeature, ArrayList<FeatureLocationBlock>>> featureFileMapping = new HashMap<>();
        //iterate over each feature and get the locations from them
        for(FeatureModelFeature feature : ReadAction.compute(()->FeatureModelUtil.findFeatures(project))) {
            //get information for the corresponding feature
            var locationMap = featureService.getFeatureFileMapping(feature);

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


    /**
     * Creates a JSON of the FeatureModel structure of the current project.
     * Depending on the mode, the JSON is structured as either hierarchical or non-hierarchical
     * @param mode
     * @return JSONObject which holds information of the FeatureModelStructure
     */
    public static JSONObject getFeatureJSON(Mode mode){
        //TODO THESIS
        // put into hans viz
        //converts TanglingMap to JSON
        FeatureService featureService = new FeatureService();

        JSONObject dataJSON = new JSONObject();
        JSONArray nodesJSON = new JSONArray();
        JSONArray linksJSON = new JSONArray();

        //get links
        //map feature with id
        HashMap<FeatureModelFeature, Integer> featureToId = new HashMap<>();
        int counter = 0;
        List<FeatureModelFeature> topLevelFeatures;


        if(mode == Mode.Default || mode == Mode.Tree || mode == Mode.TreeMap)
            topLevelFeatures = featureService.getRootFeatures();

        else if(mode == Mode.Tangling)
            topLevelFeatures = featureService.getFeatures();

        else {
            Logger.print(Logger.Channel.ERROR, "Could not create JSON because of invalid mode");
            return new JSONObject();
        }

        //TODO THESIS
        // check if reference is passed via function
        fileMappingCache = FeatureLocationManager.getAllFeatureFileMapping();
        tanglingMapCache = getTanglingMap();

        for(var feature : topLevelFeatures) {
            JSONObject featureObj = featureToJSON(feature);
            nodesJSON.add(featureObj);
            featureToId.put(feature, counter);
            counter++;
        }


        for(var featureToTangledFeatures : tanglingMapCache.entrySet()){
            for(var tangledFeature : featureToTangledFeatures.getValue()){
                //add link if id of feature is less than the id of the tangled one
                if(!featureToId.containsKey(featureToTangledFeatures.getKey()))
                    continue;
                if(featureToId.get(featureToTangledFeatures.getKey()) < featureToId.get(tangledFeature))
                {
                    JSONObject obj = new JSONObject();
                    obj.put("source", featureToTangledFeatures.getKey().getLPQText());
                    obj.put("target", tangledFeature.getLPQText());
                    linksJSON.add(obj);
                }
            }
        }
        dataJSON.put("features", nodesJSON);
        dataJSON.put("tanglingLinks", linksJSON);

        //clear caches
        fileMappingCache = null;
        tanglingMapCache = null;

        return dataJSON;
    }


    /**
     * Helperfunction to recursively create JSONObjects of features
     * Recursion takes place within the child property of the feature
     * @param feature feature which should be converted to JSON
     * @return JSONObject of given feature
     */
    private static JSONObject featureToJSON(FeatureModelFeature feature){
        //TODO THESIS
        // put into hans viz
        JSONObject obj = new JSONObject();
        obj.put("id", feature.getLPQText());
        obj.put("name", feature.getLPQText());
        var tangledFeatureMap = tanglingMapCache.get(feature);
        int tanglingDegree = tangledFeatureMap != null ? tangledFeatureMap.size() : 0;

        FeatureService featureService = new FeatureService();
        List<FeatureModelFeature> childFeatureList = featureService.getChildFeatures(feature);

        //recursively get all child features
        JSONArray childArr = new JSONArray();
        for(var child : childFeatureList){
            childArr.add(featureToJSON(child));
        }
        obj.put("children", childArr);
        obj.put("tanglingDegree", tanglingDegree);
        obj.put("lines", fileMappingCache.get(feature.getLPQText()).getTotalFeatureLineCount());
        obj.put("totalLines", featureService.getTotalLineCountWithChilds(feature));

        //put locations and their line count into array
        JSONArray locations = new JSONArray();
        var fileMappings = fileMappingCache.get(feature.getLPQText()).getAllFeatureLocations();
        for(String path : fileMappings.keySet()){
            JSONArray blocks = new JSONArray();
            for(var block : fileMappings.get(path).second){
                JSONObject blockObj = new JSONObject();
                blockObj.put("start", block.getStartLine());
                blockObj.put("end", block.getEndLine());
                blocks.add(blockObj);
            }
            //get the linecount of a feature for each file and add it
            JSONObject locationObj = new JSONObject();

            locationObj.put("lines", fileMappingCache.containsKey(path) ? fileMappingCache.get(path).getFeatureLineCountInFile(path) : 0);

            locationObj.put("blocks", blocks);
            locationObj.put(path, blocks);
            locations.add(locationObj);
        }
        obj.put("locations", locations);
        return obj;
    }
}
