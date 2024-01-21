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

package se.isselab.HAnS.metrics;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import se.isselab.HAnS.featureExtension.backgroundTask.BackgroundTask;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class FeatureTangling {

    /**
     * Returns the tangling degree of the given feature.
     * Includes expensive ReferencesSearch.search() in FeatureLocationManager.getAllFeatureFileMappings(), which might
     * cause UI freezes. Better use a BackgroundTask.
     *
     * @param feature the feature which should be checked
     * @return tangling degree of the given feature
     * @see BackgroundTask
     */
    public static int getFeatureTanglingDegree(Project project, FeatureModelFeature feature) {
        return getFeatureTanglingDegree(project, FeatureLocationManager.getAllFeatureFileMappings(project), feature);
    }

    /**
     * Returns the tangling degree of the given feature while making use of a precalculated fileMapping
     *
     * @param project      the project
     * @param fileMappings pre calculated fileMapping
     * @param feature      the feature which should be checked
     * @return tangling degree of the given feature
     */
    public static int getFeatureTanglingDegree(Project project, HashMap<String, FeatureFileMapping> fileMappings, FeatureModelFeature feature) {
        var tanglingMap = getTanglingMap(project, fileMappings);

        if (tanglingMap.containsKey(feature))
            return tanglingMap.get(feature).size();

        return 0;
    }

    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features
     *
     * @return TanglingMap
     */
    public static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(Project project) {
        return getTanglingMap(project, FeatureLocationManager.getAllFeatureFileMappings(project));
    }

    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features while making use of a precalculated fileMapping
     *
     * @param fileMappings fileMappings which should be used
     * @return TanglingMap
     */
    public static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(Project project, HashMap<String, FeatureFileMapping> fileMappings) {

        //map which contains Features and their tangled features
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = new HashMap<>();

        //map which contains file to {features and their blocks}
        HashMap<String, HashMap<FeatureModelFeature, List<FeatureLocationBlock>>> featureFileMapping = new HashMap<>();
        //iterate over each feature and get the locations from them
        for (FeatureModelFeature feature : ReadAction.compute(() -> FeatureModelUtil.findFeatures(project))) {
            //get information for the corresponding feature
            var locationMap = fileMappings.get(ReadAction.compute(feature::getLPQText));

            //create entry for the featureFileMapping - this entry contains the feature and the feature locations within the file specified by filePath

            //iterate over each file inside this map
            for (var featureLocation : locationMap.getFeatureLocations()) {
                //get the path and the corresponding feature locations within this path
                String filePath = featureLocation.getMappedPath();
                List<FeatureLocationBlock> locations = featureLocation.getFeatureLocations();

                //add the {feature to location[]} to the fileMap
                var map = featureFileMapping.get(filePath);
                if (map != null) {
                    //the file is already associated with features - check for tangling
                    for (var existingFeatureLocations : map.entrySet()) {
                        //iterate over the locations of the feature and check if any of them do intersect
                        for (FeatureLocationBlock block : locations) {
                            if (block.hasSharedLines(existingFeatureLocations.getValue().toArray(new FeatureLocationBlock[0]))) {
                                //features share the same lines of code

                                //add tangling entry for both features a->b and b->a
                                var featureB = existingFeatureLocations.getKey();

                                //add featureB to featureA
                                if (tanglingMap.containsKey(feature)) {
                                    tanglingMap.get(feature).add(featureB);
                                } else {
                                    HashSet<FeatureModelFeature> featureSet = new HashSet<>();
                                    featureSet.add(featureB);
                                    tanglingMap.put(feature, featureSet);
                                }

                                //add featureA to featureB
                                if (tanglingMap.containsKey(featureB)) {
                                    tanglingMap.get(featureB).add(feature);
                                } else {
                                    HashSet<FeatureModelFeature> featureSet = new HashSet<>();
                                    featureSet.add(feature);
                                    tanglingMap.put(featureB, featureSet);
                                }
                            }
                        }
                    }
                    //add feature to the map
                    map.put(feature, locations);

                } else {
                    //the file is new so we add a new entry
                    HashMap<FeatureModelFeature, List<FeatureLocationBlock>> featureLocationMap = new HashMap<>();
                    featureLocationMap.put(feature, locations);
                    featureFileMapping.put(filePath, featureLocationMap);
                }
            }
        }

        return tanglingMap;
    }

}
