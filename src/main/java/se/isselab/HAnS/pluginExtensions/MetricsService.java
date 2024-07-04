/*
Copyright 2024 Johan Martinson

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
import se.isselab.HAnS.pluginExtensions.backgroundTasks.MetricsCallback;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface MetricsService {

    void getProjectMetricsBackground(MetricsCallback callback);

    // region ConvenienceMethods
    /**
     * Get Feature List from HAnS in Service
     * @return Feature List of all registered features
     */
    List<FeatureModelFeature> getFeatures();

    /**
     * Returns a list of all child features of the given feature from the .feature-model
     * @param feature {@link FeatureModelFeature}
     * @return List of all child features of the given feature from the .feature-model
     */
    List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature);

    /**
     * Returns the parent feature of the given Feature from the .feature-model
     * @param feature {@link FeatureModelFeature}
     * @return Parent feature of the given Feature from the .feature-model
     */
    FeatureModelFeature getParentFeature(FeatureModelFeature feature);

    /**
     * Check if the given feature is a root feature
     * @param featureName {@link FeatureModelFeature}
     * @return true if the given feature is a root feature, false otherwise
     */
    boolean isRootFeature(FeatureModelFeature featureName);

    /**
     * Returns the top-level Feature of the given Feature from the .feature-model
     * @param feature {@link FeatureModelFeature}
     * @return Top-level Feature of the given Feature from the .feature-model
     */
    FeatureModelFeature getRootFeature(FeatureModelFeature feature);

    /**
     * Returns a list of all top-level features declared in the .feature-model
     * @return List of all top-level features declared in the .feature-model
     */
    List<FeatureModelFeature> getRootFeatures();
// endregion

    // &begin[FeatureFileMapping]
    FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature);

    void getFeatureFileMappingBackground(FeatureModelFeature feature, HAnSCallback callback);

    HashMap<String, FeatureFileMapping> getAllFeatureFileMappings();

    void getAllFeatureFileMappingsBackground(HAnSCallback callback);

    FeatureFileMapping getFeatureFileMappingOfFeature(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature);

    boolean isFeatureInFeatureFileMappings(HashMap<String, FeatureFileMapping> featureFileMappings, FeatureModelFeature feature);

    // &end[FeatureFileMapping]
    // &begin[LineCount]

    int getTotalFeatureLineCount(FeatureFileMapping featureFileMapping);

    int getFeatureLineCountInFile(FeatureFileMapping featureFileMapping, FeatureLocation featureLocation);

    // &end[LineCount]
    // &begin[Tangling]

    int getFeatureTangling(FeatureModelFeature feature);

    int getFeatureTangling(HashMap<String, FeatureFileMapping> fileMappings, FeatureModelFeature feature);

    HashSet<FeatureModelFeature> getTanglingMapOfFeature(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, FeatureModelFeature feature);

    void getTanglingMapBackground(HAnSCallback callback);

    void getFeatureTanglingBackground(FeatureModelFeature feature, HAnSCallback callback);

    HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap();

    HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(HashMap<String, FeatureFileMapping> featureFileMappings);

    // &end[Tangling]

    // &begin[Scattering]

    int getFeatureScattering(FeatureModelFeature feature);

    int getFeatureScattering(FeatureFileMapping featureFileMapping);

    void getFeatureScatteringBackground(FeatureModelFeature feature, HAnSCallback callback);

    // &end[Scattering]

    // &begin[FeatureLocation]

    ArrayList<FeatureLocation> getFeatureLocations(FeatureFileMapping featureFileMapping);

    List<FeatureLocationBlock> getListOfFeatureLocationBlock(FeatureLocation featureLocation);


    // &end[FeatureLocation]
}
