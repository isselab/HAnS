package se.isselab.HAnS.featureExtension;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.TestOnly;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;


import java.util.*;

@Service(Service.Level.PROJECT)
public final class FeatureService implements FeatureServiceInterface {
    private final Project project;

    public FeatureService(){
        this.project = ProjectManager.getInstance().getOpenProjects()[0];
    }

    /**
     * Get Feature List from HAnS in Service
     * @return Feature List of all registered features
     */
    @Override
    public List<FeatureModelFeature> getFeatures() {
        return FeatureModelUtil.findFeatures(project);
    }

    @Override
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature) {
        return null;
    }

    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getAllFeatureTangling(){
        //map which contains Features and their tangled features
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = new HashMap<>();


        //map which contains file to {features and their blocks}
        HashMap<String, HashMap<FeatureModelFeature, ArrayList<FeatureLocationBlock>>> featureFileMapping = new HashMap<>();
        //iterate over each feature and get the locations from them
        for(FeatureModelFeature feature : FeatureModelUtil.findFeatures(project)) {
            //get information for the corresponding feature
            var locationMap = FeatureLocationManager.getFeatureFileMapping(feature);

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

    @Override
    public int getFeatureTangling(FeatureModelFeature feature) {
        var resultMap = getAllFeatureTangling().get(feature);
        return resultMap != null ? resultMap.size() : 0;
    }

    @Override
    public int getFeatureScattering(FeatureModelFeature feature) {
        return 0;
    }

    @Override
    public List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature) {
        List<FeatureModelFeature> childs = new ArrayList<>();
        for(var child : feature.getChildren()) {
            childs.add((FeatureModelFeatureImpl)child);
        }
        return childs;
    }

    @Override
    public FeatureModelFeature getParentFeature(FeatureModelFeature feature) {
        if (feature.getParent() instanceof FeatureModelFile) {
            return (FeatureModelFeature) feature.getParent();
        }
        return (FeatureModelFeatureImpl) feature.getParent();
    }

    @Override
    public FeatureModelFeature getRootFeature(FeatureModelFeature feature) {
        //TODO THESIS:
        // there can be multiple top level features - maybe return FeatureModelFeature[]
        FeatureModelFeature temp = feature;
        while(!(temp.getParent() instanceof FeatureModelFile)){
            temp = (FeatureModelFeature) temp.getParent();
        }
        return temp;
    }

    @Override
    public void createFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
    }

    @Override
    public FeatureModelFeature renameFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
        return null;
    }

    @Override
    public boolean deleteFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
        return false;
    }

    @TestOnly
    public JSONObject getFeatureLineCountAsJson(){
        JSONArray featureLocationsJson = new JSONArray();
        for(var feature : FeatureModelUtil.findFeatures(project)) {

            var mapping = FeatureLocationManager.getFeatureFileMapping(feature);
            var map = mapping.getAllFeatureLocations();
            //get all lines of the given feature
            int totalLines = 0;
            for(var file : map.keySet()){
                totalLines += mapping.getFeatureLineCountInFile(file);
            }

            //convert total lines to size
            int minSize = 10;
            float size = Math.max(totalLines, minSize);

            //add options
            JSONObject attributes = new JSONObject();
            attributes.put("color", "#4f19c7");
            attributes.put("label", feature.getLPQText());
            attributes.put("attributes", new JSONArray());
            attributes.put("x", 0);
            attributes.put("y", 0);
            attributes.put("id", feature.getLPQText());
            attributes.put("size", size);

            featureLocationsJson.add(attributes);
        }
        JSONObject finalJson = new JSONObject();
        finalJson.put("nodes", featureLocationsJson);
        finalJson.put("edges", new JSONArray());

        /*for(feature : featureLocationsJson){
            JSONObject featureJson = new JSONObject();
            featureJson.put("color", "#4f19c7");
            featureJson.put("label", feature);
            featureJson.put("attributes", new JSONArray());
            featureJson.put("x", 0);
            featureJson.put("y", 0);
            featureJson.put("id", feature);

            int lineCount = feature


        }
         */
        return finalJson;
    }

    @TestOnly
    public JSONArray getFeatureModelAsJson(){
        var featureList = FeatureModelUtil.findFeatures(project);
        FeatureModelFeature root = getRootFeature(featureList.get(0));
        JSONArray data = new JSONArray();
        JSONObject rootObject = new JSONObject();

        rootObject.put("name", root.getLPQText());
        rootObject.put("value", getTotalLineCountWithChilds(root));
        rootObject.put("children", getChildFeaturesAsJson(root));
        data.add(rootObject);

        return data;
    }

    @TestOnly
    private JSONArray getChildFeaturesAsJson(FeatureModelFeature parentFeature) {
        JSONArray children = new JSONArray();
        var childFeatureList = getChildFeatures(parentFeature);

        //iterate over each child and recursively get its childs
        for(var child : childFeatureList){
            //get linecount of feature via mapping

            JSONObject childJson = new JSONObject();
            childJson.put("name", child.getLPQText());
            childJson.put("value", Math.max(getTotalLineCountWithChilds(child),1));
            childJson.put("children", getChildFeaturesAsJson(child));
            children.add(childJson);
        }
        return children;
    }

    @TestOnly
    private int getTotalLineCountWithChilds(FeatureModelFeature parent){
        int total = 1;
        for(var child : getChildFeatures(parent)){
            total += getTotalLineCountWithChilds(child);
        }
        total += FeatureLocationManager.getFeatureFileMapping(parent).getTotalFeatureLineCount();
        return total;
    }

}
