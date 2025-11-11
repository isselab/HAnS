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

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.MetricsCallback;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.GetFeatureFileMappingForFeature;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureTasks.FeatureCallback;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks.TanglingMapCallback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface MetricsService {

    /**
     * Calculates the metrics of the project in the background and returns the result to {@link MetricsCallback} implementation.
     * <p> By calling this method all metrics for each feature in the project are calculated as well.
     *
     * @param callback {@link MetricsCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    void getProjectMetricsBackground(MetricsCallback callback);

    /**
     * Calculates the metrics of a feature in the background and returns the result to {@link FeatureCallback} Implementation
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     * @param feature {@link FeatureModelFeature} for which the metrics are to be calculated
     */
    void getFeatureMetricsBackground(FeatureCallback callback, FeatureModelFeature feature);

    // region ConvenienceMethods

    /**
     * Get Feature List from HAnS in Service
     *
     * @return Feature List of all registered features
     */
    List<FeatureModelFeature> getFeatures();

    /**
     * Returns a list of all child features of the given feature from the .feature-model
     *
     * @param feature {@link FeatureModelFeature}
     * @return List of all child features of the given feature from the .feature-model
     */
    List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature);

    /**
     * Returns the parent feature of the given Feature from the .feature-model
     *
     * @param feature {@link FeatureModelFeature}
     * @return Parent feature of the given Feature from the .feature-model
     */
    FeatureModelFeature getParentFeature(FeatureModelFeature feature);

    /**
     * Check if the given feature is a root feature
     *
     * @param featureName {@link FeatureModelFeature}
     * @return true if the given feature is a root feature, false otherwise
     */
    boolean isRootFeature(FeatureModelFeature featureName);

    /**
     * Returns the top-level Feature of the given Feature from the .feature-model
     *
     * @param feature {@link FeatureModelFeature}
     * @return Top-level Feature of the given Feature from the .feature-model
     */
    FeatureModelFeature getRootFeature(FeatureModelFeature feature);

    /**
     * Returns a list of all top-level features declared in the .feature-model
     *
     * @return List of all top-level features declared in the .feature-model
     */
    List<FeatureModelFeature> getRootFeatures();
// endregion

// &begin[FeatureFileMapping]
    //region Convenience Methods

    /**
     * @param featureFileMappings All {@link FeatureFileMapping} from project as a HashMap
     * @param feature             Feature whose file mapping is to be calculated
     * @return {@link FeatureFileMapping} of Feature
     */

    FeatureFileMapping getFeatureFileMappingOfFeature(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature);

    /**
     * Checks if feature is in projects feature model, mapped as HashMap of all {@link FeatureFileMapping} of the project.
     *
     * @param featureFileMappings featureFileMappings All {@link FeatureFileMapping} from project as a HashMap
     * @param feature             feature that needs to be checked on
     * @return true if featureFileMappings contains the feature
     */

    boolean isFeatureInFeatureModel(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature);
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

    void getFeatureFileMappingBackground(FeatureModelFeature feature, FeatureFileMappingCallback callback);

    /**
     * Retrieves all {@link FeatureFileMapping} of the project in the background and returns it as a HashMap
     * to {@link FeatureFileMappingCallback} implementation.
     *
     * @param callback {@link FeatureFileMappingCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */

    void getAllFeatureFileMappingsBackground(FeatureFileMappingCallback callback);

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

    int getTotalFeatureLineCount(FeatureFileMapping featureFileMapping);

    /**
     * @param featureFileMapping {@link FeatureFileMapping}
     * @param featureLocation    {@link FeatureLocation}
     * @return total line-count of a feature in a file
     * @see FeatureFileMapping#getFeatureLineCountInFile(com.intellij.openapi.util.Pair)
     */

    int getFeatureLineCountInFile(FeatureFileMapping featureFileMapping, FeatureLocation featureLocation);
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

    HashSet<FeatureModelFeature> getTanglingMapOfFeature(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, FeatureModelFeature feature);
    //endregion

    //region Asynchronous Methods

    /**
     * Retrieves the tangled features of a feature in the background and returns the result to {@link TanglingMapCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link TanglingMapCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */
    void getFeatureTanglings(FeatureModelFeature feature, TanglingMapCallback callback);

    /**
     * Calculates tangling Degree of a feature in a background task. Result is then returned to {@link FeatureCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */

    void getFeatureTanglingDegreeBackground(FeatureModelFeature feature, FeatureCallback callback);

    /**
     * Retrieves all Tangling Maps of features in the background and returns the result to {@link TanglingMapCallback} Implementation.
     *
     * @param callback {@link TanglingMapCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */

    void getTanglingMapBackground(TanglingMapCallback callback);
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

    int getFeatureScattering(FeatureFileMapping featureFileMapping);
    //endregion

    //region Asynchronous Methods

    /**
     * Calculates the scattering degree of a feature without precalculated {@link FeatureFileMapping} in the Background and returns the result to {@link FeatureCallback} Implementation
     *
     * @param feature  {@link FeatureModelFeature}
     * @param callback {@link FeatureCallback} Implementation, on which is called <code>onComplete()</code> after finishing the BackgroundTask
     */

    void getFeatureScatteringBackground(FeatureModelFeature feature, FeatureCallback callback);
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

    void getNestingDepthsBackGround(FeatureModelFeature feature, FeatureCallback callback);
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

    void getNumberOfAnnotatedFilesBackground(FeatureModelFeature feature, FeatureCallback callback);
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

    List<FeatureLocation> getFeatureLocations(FeatureFileMapping featureFileMapping);

    /**
     * Gets {@link FeatureLocationBlock} of a {@link FeatureLocation}
     *
     * @param featureLocation {@link FeatureFileMapping} of the feature
     * @return List of {@link FeatureLocationBlock} of the {@link FeatureLocation}
     */

    List<FeatureLocationBlock> getListOfFeatureLocationBlock(FeatureLocation featureLocation);
    //endregion
    // &end[FeatureLocation]
}
