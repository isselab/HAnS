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

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public interface FeatureServiceInterface {

    List<FeatureModelFeature> getFeatures();

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
    // convenience methods
    List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature);

    FeatureModelFeature getParentFeature(FeatureModelFeature feature);

    FeatureModelFeature getRootFeature(FeatureModelFeature feature);

    List<FeatureModelFeature> getRootFeatures();

    // &begin[FileHighlighter]
    void highlightFeatureInFeatureModel(String featureLpq);

    void highlighFeatureInFeatureModel(FeatureModelFeature feature);

    void openFileInProject(String path);

    void openFileInProject(String path, int startline, int endline);
    // &end[FileHighlighter]

    void getFeatureMetricsBackground(HAnSCallback callback);
}
