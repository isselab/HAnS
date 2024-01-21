/*
Copyright 2024 David Stechow & Philipp Kusmierz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package se.isselab.HAnS.featureExtension;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;

import se.isselab.HAnS.fileHighlighter.FileHighlighter;
import se.isselab.HAnS.featureExtension.backgroundTask.*;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
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
     * Returns the locations of a Feature as a {@link FeatureFileMapping}.
     * @param feature Feature whose file mapping is to be calculated
     * @return Locations of a Feature as a {@link FeatureFileMapping}
     * @see FeatureLocationManager#getFeatureFileMapping(Project, FeatureModelFeature)
     */
    @Override
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature) {
        return FeatureLocationManager.getFeatureFileMapping(project, feature);
    }

    /**
     * @param featureFileMappings All {@link FeatureFileMapping} from project as a HashMap
     * @param feature Feature whose file mapping is to be calculated
     * @return {@link FeatureFileMapping} of Feature
     */
    @Override
    public FeatureFileMapping getFeatureFileMappingOfFeature(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature){
        return featureFileMappings.get(feature.getLPQText());
    }

    /**
     * Calculates the {@link FeatureFileMapping} of a feature in the background and returns it to {@link HAnSCallback} Implementation.
     * @param feature Feature whose file mapping is to be calculated
     * @param callback  {@link HAnSCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     * @see FileMappingBackground
     */
    @Override
    public void getFeatureFileMappingBackground(FeatureModelFeature feature, HAnSCallback callback) {
        BackgroundTask task = new FileMappingBackground(project, "Scanning features", callback, new FeatureMetrics(feature));
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    /**
     * Calculates all {@link FeatureFileMapping} of the project
     * @return HashMap of all {@link FeatureFileMapping} of the project
     * @see FeatureLocationManager#getAllFeatureFileMappings(Project)
     */
    @Override
    public HashMap<String, FeatureFileMapping> getAllFeatureFileMappings(){
        return FeatureLocationManager.getAllFeatureFileMappings(project);
    }

    /**
     * Calculates all {@link FeatureFileMapping} of the project in the background and returns it as a HashMap
     * to {@link HAnSCallback} Implementation.
     * @param callback {@link HAnSCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getAllFeatureFileMappingsBackground(HAnSCallback callback){
        BackgroundTask task = new FeatureFileMappingsBackground(project, "Scanning features", callback, null);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    /**
     * Checks if feature is in projects feature model, mapped as HashMap of all {@link FeatureFileMapping} of the project.
     * @param featureFileMappings featureFileMappings All {@link FeatureFileMapping} from project as a HashMap
     * @param feature feature that needs to be checked on
     * @return true if featureFileMappings contains the feature
     */
    @Override
    public boolean isFeatureInFeatureFileMappings(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature){
        return featureFileMappings.containsKey(feature.getLPQText());
    }
    // &end[FeatureFileMapping]

    // &begin[LineCount]
    /**
     * Method to get the total line-count of a feature for all files
     * @param featureFileMapping {@link FeatureFileMapping} of the feature
     * @return line-count of a feature
     */
    @Override
    public int getTotalFeatureLineCount(FeatureFileMapping featureFileMapping){
        return featureFileMapping.getTotalFeatureLineCount();
    }
    /**
     * @param featureFileMapping {@link FeatureFileMapping}
     * @param featureLocation {@link FeatureLocation}
     * @return total line-count of a feature in a file
     * @see FeatureFileMapping#getFeatureLineCountInFile(String)
     */
    @Override
    public int getFeatureLineCountInFile(FeatureFileMapping featureFileMapping, FeatureLocation featureLocation){
        return featureFileMapping.getFeatureLineCountInFile(featureLocation.getMappedPath());
    }
    // &end[LineCount]

    // &begin[Tangling]
    /**
     * Returns the tangling degree of the given feature.
     * Use this method only if you want to calculate it for one feature.
     * Otherwise, use {@link #getFeatureTangling(HashMap, FeatureModelFeature)} so that the featureFileMappings is only calculated once
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
     * @param featureFileMappings All {@link FeatureFileMapping} of the project as HashMap
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
     * Convenient Method for getting the tangling Map of one Feature as a HashSet.
     * @param tanglingMap All tangling maps of the project as a HashMap
     * @param feature {@link FeatureModelFeature}
     * @return Tangling map of a feature as a HashSet
     */
    @Override
    public HashSet<FeatureModelFeature> getTanglingMapOfFeature(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, FeatureModelFeature feature){
        return tanglingMap.get(feature);
    }
    /**
     * Calculates tangling Degree of a feature in a background task. Result is then returned to {@link HAnSCallback} Implementation
     * @param feature {@link FeatureModelFeature}
     * @param callback {@link HAnSCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
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
        return FeatureTangling.getTanglingMap(project);
    }

    /**
     *  Calculates all Tangling Maps of features in the background and returns the result to {@link HAnSCallback} Implementation.
     * @param callback {@link HAnSCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getTanglingMapBackground(HAnSCallback callback){
        BackgroundTask task = new TanglingMapBackground(project, "Scanning features", callback, null);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }
    /**
     * @see FeatureTangling#getTanglingMap(Project, HashMap)
     * @param featureFileMappings All {@link FeatureFileMapping} of the project
     * @return the tanglingMap of the features represented by the fileMapping
     */
    @Override
    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(HashMap<String, FeatureFileMapping> featureFileMappings){
        return FeatureTangling.getTanglingMap(project, featureFileMappings);
    }
    // &end[Tangling]

    // &begin[Scattering]

    /**
     * @see FeatureScattering#getScatteringDegree(Project, FeatureModelFeature)
     * @param feature {@link FeatureModelFeature}
     * @return scattering degree of the feature
     */
    @Override
    public int getFeatureScattering(FeatureModelFeature feature) {
        return FeatureScattering.getScatteringDegree(project, feature);
    }

    /**
     * Calculates the scattering degree of a feature based on the {@link FeatureFileMapping} of the feature
     * @see FeatureScattering#getScatteringDegree(FeatureFileMapping)
     * @param featureFileMapping {@link FeatureFileMapping} of the feature
     * @return scattering degree of the feature represented by the file mapping
     */
    @Override
    public int getFeatureScattering(FeatureFileMapping featureFileMapping) {
        return FeatureScattering.getScatteringDegree(featureFileMapping);
    }

    /**
     * Calculates the scattering degree of a feature without precalculated {@link FeatureFileMapping} in the Background and returns the result to {@link HAnSCallback} Implementation
     * @param feature {@link FeatureModelFeature}
     * @param callback {@link HAnSCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getFeatureScatteringBackground(FeatureModelFeature feature, HAnSCallback callback) {
        BackgroundTask task = new ScatteringDegreeBackground(project, "Scanning features", callback, new FeatureMetrics(feature));
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

    // &end[Scattering]

    // &begin[FeatureLocation]
    /**
     * Finds {@link FeatureLocation} of the feature
     * @param featureFileMapping {@link FeatureFileMapping} of the feature
     * @return ArrayList of {@link FeatureLocation} of the Feature
     */
    @Override
    public ArrayList<FeatureLocation> getFeatureLocations(FeatureFileMapping featureFileMapping){
        return featureFileMapping.getFeatureLocations();
    }
    /**
     * Gets {@link FeatureLocationBlock} of a {@link FeatureLocation}
     * @param featureLocation {@link FeatureFileMapping} of the feature
     * @return List of {@link FeatureLocationBlock} of the {@link FeatureLocation}
     */
    @Override
    public List<FeatureLocationBlock> getListOfFeatureLocationBlock(FeatureLocation featureLocation){
        return featureLocation.getFeatureLocations();
    }
    // &end[FeatureLocation]

    /**
     * Returns a list of all child features of the given feature from the .feature-model
     * @param feature {@link FeatureModelFeature}
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
     * @param feature {@link FeatureModelFeature}
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
     * @param feature {@link FeatureModelFeature}
     * @return Top-level Feature of the given Feature from the .feature-model
     */
    @Override
    public FeatureModelFeature getRootFeature(FeatureModelFeature feature) {
        FeatureModelFeature temp = feature;
        while(!(temp.getParent() instanceof FeatureModelFile)){
            temp = (FeatureModelFeature) temp.getParent();
        }
        return temp;
    }

    /**
     * Returns a list of all top-level features declared in the .feature-model
     * @return List of all top-level features declared in the .feature-model
     */
    @Override
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

    // &begin[FileHighlighter]
    /**
     * Highlights a feature in the feature model
     * @param featureLpq LPQ name of the feature
     * @see FileHighlighter#highlightFeatureInFeatureModel(Project, String)
     */
    @Override
    public void highlightFeatureInFeatureModel(String featureLpq) {

        FileHighlighter.highlightFeatureInFeatureModel(project, featureLpq);
    }
    /**
     * Highlights a feature in the feature model
     * @param feature {@link FeatureModelFeature}
     * @see FileHighlighter#highlightFeatureInFeatureModel(FeatureModelFeature)
     */
    @Override
    public void highlighFeatureInFeatureModel(FeatureModelFeature feature){
        FileHighlighter.highlightFeatureInFeatureModel(feature);
    }

    /**
     * Opens a file of the project in the editor
     * @param path String: absolute path of the file
     */
    @Override
    public void openFileInProject(String path){
        FileHighlighter.openFileInProject(project, path);
    }
    /**
     * Opens a file of the project in the editor and highlights code block
     * @param path String: Absolute path of the file
     * @param startline of the codeblock
     * @param endline of the codeblock
     */
    @Override
    public void openFileInProject(String path, int startline, int endline){
        FileHighlighter.openFileInProject(project, path, startline, endline);
    }
    // &end[FileHighlighter]

     /** Generate all {@link FeatureFileMapping} of the project and tanglingMap for the whole project in the background and returns it to {@link HAnSCallback} Implementation.
     * @param callback {@link HAnSCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getFeatureMetricsBackground(HAnSCallback callback) {
        BackgroundTask task = new FeatureMetricsBackground(project, "Scanning features", callback, null);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }

}
