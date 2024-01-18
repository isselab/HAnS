package se.isselab.HAnS.featureExtension;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.TestOnly;
import se.isselab.HAnS.featureExtension.backgroundTask.*;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.metrics.FeatureMetrics;
import se.isselab.HAnS.metrics.FeatureScattering;
import se.isselab.HAnS.metrics.FeatureTangling;


import java.util.*;

@Service(Service.Level.PROJECT)
public final class FeatureService implements FeatureServiceInterface {
    private final Project project;

    public FeatureService(Project project){
        this.project = project;
    }

    /**
     * Get Feature List from HAnS in Service
     * @return Feature List of all registered features
     */
    @Override
    public List<FeatureModelFeature> getFeatures() {
        return FeatureModelUtil.findFeatures(project);
    }

    // &begin[FeatureFileMapping]
    /**
     * Returns the locations of a Feature as a FeatureFileMapping
     * @param feature
     * @return Locations of a Feature as a FeatureFileMapping
     */
    @Override
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature) {
        return FeatureLocationManager.getFeatureFileMapping(project, feature);
    }

    @Override
    public void getFeatureFileMappingBackground(FeatureModelFeature feature, HAnSCallback callback) {
        BackgroundTask task = new FileMappingBackground(project, "Scanning features", callback, new FeatureMetrics(feature));
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    // TODO: Expensive
    /**
     * expensive
     * @return
     */
    @Override
    public HashMap<String, FeatureFileMapping> getAllFeatureFileMappings(){
        System.out.println("called service.getAllFeatureFileMappings");
        return FeatureLocationManager.getAllFeatureFileMappings(project);
    }

    // TODO THESIS: preparation for background task
    @Override
    public void getAllFeatureFileMappingsBackground(HAnSCallback callback){
        BackgroundTask task = new FeatureFileMappingsBackground(project, "Scanning features", callback, null);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }
    // &end[FeatureFileMapping]

    // &begin[Tangling]
    /**
     * Returns the tangling degree of the given feature.
     * Use this method only if you want to calculate it for one feature. Otherwise, use {@link #getFeatureTangling(HashMap, FeatureModelFeature)} so that the featureFileMappings is only calculated once
     * @param feature FeatureModelFeature
     * @return TanglingDegree of the given feature
     * @see FeatureFileMapping
     * @see #getFeatureTangling(HashMap, FeatureModelFeature)
     */
    @Override
    public int getFeatureTangling(FeatureModelFeature feature) {
        var resultMap = getTanglingMap().get(feature);
        return resultMap != null ? resultMap.size() : 0;
    }

    /**
     * Returns the tangling degree of the given feature. Uses pre-calculated HashMap of feature file mappings
     * @param featureFileMappings
     * @param feature FeatureModelFeature
     * @return TanglingDegree of the given feature
     * @see FeatureFileMapping
     * @see #getFeatureTangling(HashMap, FeatureModelFeature)
     */
    @Override
    public int getFeatureTangling(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature) {
        var resultMap = getTanglingMap(featureFileMappings).get(feature);
        return resultMap != null ? resultMap.size() : 0;
    }

    /**
     *
     * @param feature
     * @param callback
     * @return
     */
    @Override
    public void getFeatureTanglingBackground(FeatureModelFeature feature, HAnSCallback callback) {
        BackgroundTask task = new TanglingDegreeBackground(project, "Scanning features", callback, new FeatureMetrics(feature));
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }


    /**
     * Uses expensive method ReferencesSearch.search(), which can cause UI freezes. Maybe use a BackgroundTask instead.
     * @see FeatureTangling#getTanglingMap(Project)
     * @see #getTanglingMapBackground(HAnSCallback)
     * @return the tanglingMap of all features
     */
    @Override
    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(){
        System.out.println("called service.getTanglingMap");
        return FeatureTangling.getTanglingMap(project);
    }
    public void getTanglingMapBackground(HAnSCallback callback){
        BackgroundTask task = new TanglingMapBackground(project, "Scanning features", callback, null);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }
    @Override
    /**
     * @see FeatureTangling#getTanglingMap(HashMap)
     * @param featureFileMappings
     * @return the tanglingMap of the features represented by the fileMapping
     */
    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(HashMap<String, FeatureFileMapping> featureFileMappings){
        System.out.println("called service.getTanglingMap");
        return FeatureTangling.getTanglingMap(project, featureFileMappings);
    }
    // &end[Tangling]

    // &begin[Scattering]
    /**
     * Returns the scattering degree of the given feature
     * @param feature
     * @return Scattering degree of the given feature
     */

    /**
     * @see FeatureScattering#getScatteringDegree(Project, FeatureModelFeature)
     * @param feature
     * @return scattering degree of the feature
     */
    @Override
    public int getFeatureScattering(FeatureModelFeature feature) {
        return FeatureScattering.getScatteringDegree(project, feature);
    }

    /**
     * @see FeatureScattering#getScatteringDegree(FeatureFileMapping)
     * @param featureFileMapping
     * @return scattering degree of the feature represented by the file mapping
     */
    @Override
    public int getFeatureScattering(FeatureFileMapping featureFileMapping) {
        return FeatureScattering.getScatteringDegree(featureFileMapping);
    }

    @Override
    public void getFeatureScatteringBackground(FeatureModelFeature feature, HAnSCallback callback) {
        BackgroundTask task = new ScatteringDegreeBackground(project, "Scanning features", callback, new FeatureMetrics(feature));
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    // &end[Scattering]

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
        //TODO THESIS: As there is only one root feature, it may also be possible to obtain the root feature via the feature model.
        // Each project has only one root feature (as long as there is only one feature model for a project)
        FeatureModelFeature temp = feature;
        while(!(temp.getParent() instanceof FeatureModelFile)){
            temp = (FeatureModelFeature) temp.getParent();
        }
        return temp;
    }

    // TODO: use this for only one feature
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
    // &begin[Referencing]
    @Override
    public FeatureModelFeature renameFeature(FeatureModelFeature feature, String newName) {
        // TODO: use existing function of HAnS
        feature.setName(newName);
        return null;
    }

    @Override
    public boolean deleteFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
        return false;
    }
    // &end[Referencing]
    // TODO: delete?
    @TestOnly
    public JSONObject getFeatureLineCountAsJson(){
        JSONArray featureLocationsJson = new JSONArray();
        for(var feature : FeatureModelUtil.findFeatures(project)) {

            var mapping = FeatureLocationManager.getFeatureFileMapping(project, feature);
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

    /**
     * Generate featureFileMappings and tanglingMap
     * @param callback
     */
    @Override
    public void getFeatureMetricsBackground(HAnSCallback callback) {
        BackgroundTask task = new FeatureMetricsBackground(project, "Scanning features", callback, null);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

}
