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
package se.isselab.HAnS.metrics.calculators;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.*;

import static se.isselab.HAnS.featureLocation.FeatureLocationManager.getAllFeatureFileMappings;

/**
 * The metric Nesting Depths of annotations has three dimensions: Maximum (MaxND), Minimum (MinND), and Average (AvgND) nesting depth.
 * Nesting depth expresses the fact, how deep a specific feature annotation is nested - completely
 * or partially - with another feature annotation. The depth of nesting is 1, when the annotations
 * are neither inside another textual annotation (e.g. &begin / &end) nor the containing file or any
 * (parent-)folder contains a feature annotation. Each textual, le and folder annotation increases
 * the nesting depth by 1.
 */
public class NestingDepths {

    public static List<Pair<String, Integer>> getFeatureNestingDepths(Project project, FeatureModelFeature feature) {
        var nestingDepthMap = getNestingDepthMap(FeatureLocationManager.getAllFeatureFileMappings(project));
        var featureLPQ = ReadAction.compute(feature::getLPQText);
        return nestingDepthMap.containsKey(featureLPQ)? nestingDepthMap.get(featureLPQ).stream().toList(): null;
    }

    public static Map<String, List<Pair<String, Integer>>> getNestingDepthMap(HashMap<String, FeatureFileMapping> fileMappings) {
        // Key = FeatureLPQ -> Value = List of Pair<FilePath, NestingDepth>
        var nestingDepthMap = new HashMap<String, List<Pair<String, Integer>>>();

        // FilePath -> Feature -> Location
        for (var fileMapping : fileMappings.values()) {
            var featureLPQ = ReadAction.compute(fileMapping.getFeature()::getLPQText);


            for (var featureLocation: fileMapping.getFeatureLocations()) {
                var filePath = featureLocation.getMappedPath();
                var nestingDepth = 1;

                var projectFeatureLocations = fileMappings.values().stream().map(FeatureFileMapping::getFeatureLocations)
                        .flatMap(List::stream).toList().stream().filter(f -> f.getMappedPath().equals(filePath)).toList();

                var fileFeatureLocations =
                        projectFeatureLocations.stream().map(FeatureLocation::getFeatureLocations).flatMap(List::stream).toList();

                for (var block : featureLocation.getFeatureLocations()) {
                    var fileFeatureLocationsExceptThisBlock = fileFeatureLocations.stream().filter(f -> f != block).toList();
                    nestingDepth += block.countTimesInsideOfBlocks(fileFeatureLocationsExceptThisBlock);
                }

                if(nestingDepthMap.containsKey(featureLPQ)){
                    nestingDepthMap.get(featureLPQ).add(new Pair<>(filePath, nestingDepth));
                }else {
                    var list = new LinkedList<Pair<String, Integer>>();
                    list.add(new Pair<>(filePath, nestingDepth));
                    nestingDepthMap.put(featureLPQ, list);
                }
            }
        }

        return nestingDepthMap;
    }
}
