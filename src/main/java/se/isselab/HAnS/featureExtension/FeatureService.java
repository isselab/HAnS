package se.isselab.HAnS.featureExtension;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.TestOnly;
import se.isselab.HAnS.HAnSCallback;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.metrics.FeatureTangling;


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


    /**
     * Returns the locations of a Feature as a FeatureFileMapping
     * @param feature
     * @return Locations of a Feature as a FeatureFileMapping
     */
    @Override
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature) {
        return FeatureLocationManager.getFeatureFileMapping(feature);
    }

    public HashMap<String, FeatureFileMapping> getAllFeatureFileMappings(){
        System.out.println("called service.getAllFeatureFileMappings");
        return FeatureLocationManager.getAllFeatureFileMapping();
    }
    // TODO THESIS: preparation for background task
    public HashMap<String, FeatureFileMapping> getAllFeatureFileMappings(HAnSCallback callback){
        return FeatureLocationManager.getAllFeatureFileMapping();
    }


    /**
     * Returns the tanling degree of the given feature
     * @param feature
     * @return TanglingDegree of the given feature
     */
    @Override
    public int getFeatureTangling(FeatureModelFeature feature) {
        var resultMap = getTanglingMap().get(feature);
        return resultMap != null ? resultMap.size() : 0;
    }

    /**
     * Returns the scattering degree of the given feature
     * @param feature
     * @return Scattering degree of the given feature
     */
    @Override
    public int getFeatureScattering(FeatureModelFeature feature) {
        return 0;
    }

    /**
     * Returns a list of all child features of the given feature from the .feature-model
     * @param feature
     * @return List of all child features of the given feature from the .feature-model
     */
    @Override
    public List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature) {
        List<FeatureModelFeature> childs = new ArrayList<>();
        for(var child : feature.getChildren()) {
            childs.add((FeatureModelFeatureImpl)child);
        }
        return childs;
    }

    /**
     * Returns the parent feature of the given Feature from the .feature-model
     * @param feature
     * @return Parent feature of the given Feature from the .feature-model
     */
    @Override
    public FeatureModelFeature getParentFeature(FeatureModelFeature feature) {
        if (feature.getParent() instanceof FeatureModelFile) {
            return (FeatureModelFeature) feature.getParent();
        }
        return (FeatureModelFeatureImpl) feature.getParent();
    }

    /**
     * Returns the top-level Feature of the given Feature from the .feature-model
     * @param feature
     * @return Top-level Feature of the given Feature from the .feature-model
     */
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

    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features
     * @return TanglingMap
     */
    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(){
        System.out.println("called service.getTanglingMap");
        return FeatureTangling.getTanglingMap();
    }

    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(HashMap<String, FeatureFileMapping> fileMappings){
        System.out.println("called service.getTanglingMap");
        return FeatureTangling.getTanglingMap(fileMappings);
    }

    /**
     * Returns a list of all top-level features declared in the .feature-model
     * @return List of all top-level features declared in the .feature-model
     */
    public List<FeatureModelFeature> getRootFeatures(){
        var featureList = FeatureModelUtil.findFeatures(project);
        ArrayList<FeatureModelFeature> rootFeatures = new ArrayList<>();

        if(featureList.isEmpty())
            return rootFeatures;

        FeatureModelFeature entryFeature = featureList.get(0);
        rootFeatures.add(entryFeature);

        //traverse left siblings
        FeatureModelFeature siblingFeature = entryFeature;
        while(siblingFeature.getPrevSibling() instanceof FeatureModelFeature){
            siblingFeature = (FeatureModelFeature) siblingFeature.getPrevSibling();
            rootFeatures.add(siblingFeature);
        }

        //traverse right siblings
        siblingFeature = entryFeature;
        while(siblingFeature.getNextSibling() instanceof FeatureModelFeature){
            siblingFeature = (FeatureModelFeature) siblingFeature.getNextSibling();
            rootFeatures.add(siblingFeature);
        }

        return rootFeatures;
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
            childJson.put("value", getTotalLineCountWithChilds(child));
            childJson.put("children", getChildFeaturesAsJson(child));
            children.add(childJson);
        }
        return children;
    }

    @TestOnly
    public int getTotalLineCountWithChilds(FeatureModelFeature parent){
        //TODO THESIS
        // put into hans viz
        int total = 0;
        for(var child : getChildFeatures(parent)){
            total += getTotalLineCountWithChilds(child);
        }
        total += FeatureLocationManager.getFeatureFileMapping(parent).getTotalFeatureLineCount();
        return total;
    }

    public void getFeatureFileMappingAndTanglingMap(HAnSCallback callback) {
        BackgroundTask task = new BackgroundTask(project, "Scanning features", callback, (Mode.FILEMAPPING|Mode.TANGLINGMAP));
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

}
