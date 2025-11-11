/*
Copyright 2024 Johan Martinson, David Stechow & Philipp Kusmierz

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

package se.isselab.HAnS.pluginExtensions;

import com.intellij.openapi.components.Service;

import com.intellij.openapi.project.Project;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.MetricsCallback;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.GetProjectMetrics;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.GetFeatureFileMappingForFeature;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.GetFeatureFileMappings;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureTasks.*;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks.GetTangledFeaturesForFeature;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks.GetTanglingMap;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks.TanglingMapCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Service(Service.Level.PROJECT)
public final class ProjectMetricsService implements MetricsService {

    private final Project project;

    public ProjectMetricsService(Project project) {
        this.project = project;
    }

    /**
     * Calculates the metrics of the project in the background and returns the result to {@link MetricsCallback} Implementation
     *
     * @param callback {@link MetricsCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getProjectMetricsBackground(MetricsCallback callback) {
        new GetProjectMetrics(project, "Refreshing metrics...", callback, null).queue();
    }

    /**
     * Calculates the metrics of a feature in the background and returns the result to {@link FeatureCallback} Implementation
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     * @param feature {@link FeatureModelFeature} for which the metrics are to be calculated
     */
    @Override
    public void getFeatureMetricsBackground(FeatureCallback callback, FeatureModelFeature feature) {
        var title = String.format("Calculating Metrics for %s", feature.getLPQText());
        new GetFeatureMetricsForFeature(project, title, callback, feature).queue();
    }

    //region Convenience Methods
    @Override
    public List<FeatureModelFeature> getFeatures() {
        return FeatureModelUtil.findFeatures(project);
    }

    @Override
    public List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature) {
        List<FeatureModelFeature> childs = new ArrayList<>();
        for (var child : feature.getChildren()) {
            childs.add((FeatureModelFeatureImpl) child);
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
    public boolean isRootFeature(FeatureModelFeature feature) {
        return feature.getParent() instanceof FeatureModelFile;
    }

    @Override
    public FeatureModelFeature getRootFeature(FeatureModelFeature feature) {
        FeatureModelFeature temp = feature;
        while (!(temp.getParent() instanceof FeatureModelFile)) {
            temp = (FeatureModelFeature) temp.getParent();
        }
        return temp;
    }

    @Override
    public List<FeatureModelFeature> getRootFeatures() {
        var featureList = FeatureModelUtil.findFeatures(project);
        ArrayList<FeatureModelFeature> rootFeatures = new ArrayList<>();

        if (featureList.isEmpty())
            return rootFeatures;

        FeatureModelFeature siblingFeature = featureList.getFirst();
        rootFeatures.add(siblingFeature);

        //traverse left siblings
        while (siblingFeature.getPrevSibling() instanceof FeatureModelFeature featureModelFeature) {
            rootFeatures.add(featureModelFeature);
        }

        //traverse right siblings
        while (siblingFeature.getNextSibling() instanceof FeatureModelFeature featureModelFeature) {
            rootFeatures.add(featureModelFeature);
        }

        return rootFeatures;
    }
    //endregion

    // &begin[FeatureFileMapping]
    //region Convenience Methods

    /**
     * @param featureFileMappings All {@link FeatureFileMapping} from project as a HashMap
     * @param feature             Feature whose file mapping is to be calculated
     * @return {@link FeatureFileMapping} of Feature
     */
    @Override
    public FeatureFileMapping getFeatureFileMappingOfFeature(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature) {
        return featureFileMappings.get(feature.getLPQText());
    }

    /**
     * Checks if feature is in projects feature model, mapped as HashMap of all {@link FeatureFileMapping} of the project.
     *
     * @param featureFileMappings featureFileMappings All {@link FeatureFileMapping} from project as a HashMap
     * @param feature             feature that needs to be checked on
     * @return true if featureFileMappings contains the feature
     */
    @Override
    public boolean isFeatureInFeatureModel(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature) {
        return featureFileMappings.containsKey(feature.getLPQText());
    }
    //endregion

    //region Asynchronous Methods

    /**
     * Retrieves the {@link FeatureFileMapping} of a feature in the background and returns it to {@link FeatureFileMappingCallback} implementation.
     *
     * @param feature  Feature whose file mapping is to be calculated
     * @param callback {@link FeatureFileMappingCallback} implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     * @see GetFeatureFileMappingForFeature
     * @see com.intellij.openapi.progress.Task.Backgroundable
     */
    @Override
    public void getFeatureFileMappingBackground(FeatureModelFeature feature, FeatureFileMappingCallback callback) {
        var title = String.format("Calculating the Feature locations for %s", feature.getLPQText());
        new GetFeatureFileMappingForFeature(project, title, callback, feature).queue();
    }

    /**
     * Retrieves all {@link FeatureFileMapping} of the project in the background and returns it as a HashMap
     * to {@link FeatureFileMappingCallback} implementation.
     *
     * @param callback {@link FeatureFileMappingCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getAllFeatureFileMappingsBackground(FeatureFileMappingCallback callback) {
        new GetFeatureFileMappings(project, "Retrieving Feature locations", callback).queue();
    }

    //endregion
    // &end[FeatureFileMapping]

    // &begin[LineCount]
    //region Convenience Methods

    /**
     * Method to get the total line-count of a feature for all files
     *
     * @param featureFileMapping {@link FeatureFileMapping} of the feature
     * @return line-count of a feature
     */
    @Override
    public int getTotalFeatureLineCount(FeatureFileMapping featureFileMapping) {
        return featureFileMapping.getTotalFeatureLineCount();
    }

    /**
     * @param featureFileMapping {@link FeatureFileMapping}
     * @param featureLocation    {@link FeatureLocation}
     * @return total line-count of a feature in a file
     * @see FeatureFileMapping#getFeatureLineCountInFile(com.intellij.openapi.util.Pair)
     */
    @Override
    public int getFeatureLineCountInFile(FeatureFileMapping featureFileMapping, FeatureLocation featureLocation) {
        return featureFileMapping.getFeatureLineCountInFile(featureLocation.getMappedPathPairMappedBy());
    }
    //endregion
    // &end[LineCount]

    // &begin[Tangling]
    //region Convenience Methods

    /**
     * Convenient Method for getting the tangling Map of one Feature as a HashSet.
     *
     * @param tanglingMap All tangling maps of the project as a HashMap
     * @param feature     {@link FeatureModelFeature}
     * @return Tangling map of a feature as a HashSet
     */
    @Override
    public HashSet<FeatureModelFeature> getTanglingMapOfFeature(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, FeatureModelFeature feature) {
        return tanglingMap.get(feature);
    }
    //endregion

    //region Asynchronous Methods

    /**
     * Retrieves the tangled features of a feature in the background and returns the result to {@link TanglingMapCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link TanglingMapCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    public void getFeatureTanglings(FeatureModelFeature feature, TanglingMapCallback callback) {
        var title = String.format("Retrieve tangled features of %s", feature.getLPQText());
        new GetTangledFeaturesForFeature(project, title, callback, feature).queue();
    }

    /**
     * Calculates tangling Degree of a feature in a background task. Result is then returned to {@link FeatureCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getFeatureTanglingDegreeBackground(FeatureModelFeature feature, FeatureCallback callback) {
        var title = String.format("Calculating Tangling Degree for %s", feature.getLPQText());
        new GetTanglingDegreeForFeature(project, title, callback, feature).queue();
    }

    /**
     * Retrieves all Tangling Maps of features in the background and returns the result to {@link TanglingMapCallback} Implementation.
     *
     * @param callback {@link TanglingMapCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getTanglingMapBackground(TanglingMapCallback callback) {
        new GetTanglingMap(project, "Retrieving Tangled Features", callback).queue();
    }
    //endregion
    // &end[Tangling]

    // &begin[Scattering]
    //region Convenience Methods

    /**
     * Calculates the scattering degree of a feature based on the {@link FeatureFileMapping} of the feature
     *
     * @param featureFileMapping {@link FeatureFileMapping} of the feature
     * @return scattering degree of the feature represented by the file mapping
     * @see FeatureScattering#getScatteringDegree(FeatureFileMapping)
     */
    @Override
    public int getFeatureScattering(FeatureFileMapping featureFileMapping) {
        return FeatureScattering.getScatteringDegree(featureFileMapping);
    }
    //endregion

    //region Asynchronous Methods
    /**
     * Calculates the scattering degree of a feature without precalculated {@link FeatureFileMapping} in the Background and returns the result to {@link FeatureCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getFeatureScatteringBackground(FeatureModelFeature feature, FeatureCallback callback) {
        var title = String.format("Calculating Scattering Degree for %s", feature.getLPQText());
        new GetScatteringDegreeForFeature(project, title, callback, feature).queue();
    }
    //endregion
    // &end[Scattering]

    //&begin[NestingDepths]
    //region Asynchronous Methods
    /**
     * Calculates the Nesting depths of a feature in the background and returns the result to {@link FeatureCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getNestingDepthsBackGround(FeatureModelFeature feature, FeatureCallback callback) {
        var title = String.format("Calculating Nesting depths for %s", feature.getLPQText());
        new GetNestingDepthsForFeature(project, title, callback, feature).queue();
    }
    //endRegion
    //&end[NestingDepths]

    // &begin[NumberOfAnnotatedFiles]
    //region Asynchronous Methods

    /**
     * Retrieves the number of annotated files of a feature in the background and returns the result to {@link FeatureCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    @Override
    public void getNumberOfAnnotatedFilesBackground(FeatureModelFeature feature, FeatureCallback callback) {
        var title = String.format("Calculating Number of Annotated Files for %s", feature.getLPQText());
        new GetNumberOfAnnotationsForFeature(project, title, callback, feature).queue();
    }
    //endRegion
    // &end[NumberOfAnnotatedFiles]

    // &begin[FeatureLocation]
    //region Convenience Methods

    /**
     * Finds {@link FeatureLocation} of the feature
     *
     * @param featureFileMapping {@link FeatureFileMapping} of the feature
     * @return ArrayList of {@link FeatureLocation} of the Feature
     */
    @Override
    public List<FeatureLocation> getFeatureLocations(FeatureFileMapping featureFileMapping) {
        return featureFileMapping.getFeatureLocations();
    }

    /**
     * Gets {@link FeatureLocationBlock} of a {@link FeatureLocation}
     *
     * @param featureLocation {@link FeatureFileMapping} of the feature
     * @return List of {@link FeatureLocationBlock} of the {@link FeatureLocation}
     */
    @Override
    public List<FeatureLocationBlock> getListOfFeatureLocationBlock(FeatureLocation featureLocation) {
        return featureLocation.getFeatureLocations();
    }
    //endregion
    // &end[FeatureLocation]
}
