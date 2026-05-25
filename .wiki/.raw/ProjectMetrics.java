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
package se.isselab.HAnS.metrics;

import com.intellij.openapi.util.Pair;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectMetrics {

    private final Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap; // The tangled features of entire project
    private final Map<String, FeatureFileMapping> featureFileMappings; // The FeatureFileMappings of entire project, string = path and FeatureFileMapping =
    private final Map<String, List<Pair<String, Integer>>> nestingDepthMap; // The nesting depth of entire project
    private final int NumberOfFeatures;
    private final int NumberOfAnnotatedFiles;
    private final double AvgScatteringDegree;
    private final double AvgLinesOfFeatureCode;
    private final double AvgNestingDepth;

    /**
     * Generate Feature Metrics with FeatureFileMappings and TanglingMap
     *
     * @param featureFileMappings {@link FeatureFileMapping} of all {@link FeatureModelFeature} represented as HashMap
     * @param tanglingMap         Tangling Map of all {@link FeatureModelFeature} represented as HashMap
     * @param nestingDepthMap    Nesting Depth Map of all {@link FeatureModelFeature} represented as HashMap
     */
    public ProjectMetrics(Map<String, FeatureFileMapping> featureFileMappings, Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, Map<String, List<Pair<String, Integer>>> nestingDepthMap) {
        this.tanglingMap = tanglingMap; // &line[Tangling]
        this.featureFileMappings = featureFileMappings; // &line[FeatureFileMapping]
        this.nestingDepthMap = nestingDepthMap; // &line[NestingDepths]

        this.NumberOfFeatures = featureFileMappings.size(); // &line[NumberOfFeatures]

        var featLocations = new HashSet<String>();
        featureFileMappings.values().stream().map(FeatureFileMapping::getMappedFilePaths)
                .forEach(featLocations::addAll);
        this.NumberOfAnnotatedFiles = featLocations.size(); // &line[NumberOfAnnotatedFiles]

        // &begin[Scattering]
        AvgScatteringDegree = featureFileMappings.values().stream()
                .map(FeatureFileMapping::getFeature)
                .mapToInt(feature -> feature != null ? feature.getScatteringDegree() : 0)
                .average().orElse(0);
        // &end[Scattering]

        // &begin[LineCount]
        AvgLinesOfFeatureCode = featureFileMappings.values().stream()
                .map(FeatureFileMapping::getFeature)
                .mapToInt(feature -> feature != null ? feature.getLineCount() : 0)
                .average().orElse(0);
        // &end[LineCount]

        // &begin[NestingDepths]
        var allNestingDepths = this.nestingDepthMap.values().stream().flatMap(List::stream).toList()
                .stream().map(x -> x.getSecond()).toList().stream();
        AvgNestingDepth = allNestingDepths.mapToInt(Integer::intValue).average().orElse(0);
        // &end[NestingDepths]
    }

    /**
     * @return TanglingMap of the Feature Model or null if no TanglingMap was calculated
     */
    public Map<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap() {
        return tanglingMap;
    }

    /**
     * @return FeatureFileMappings of the Feature Model or null if no FeatureFileMappings was calculated
     */
    public Map<String, FeatureFileMapping> getFeatureFileMappings() {
        return featureFileMappings;
    }

    public Collection<FeatureModelFeature> getFeaturesInProject(){
        return  featureFileMappings.values().stream().map(FeatureFileMapping::getFeature).collect(Collectors.toSet());
    }

    public Map<String, List<Pair<String, Integer>>> getNestingDepthMap() {
        return nestingDepthMap;
    }

    public int getNumberOfFeatures() {
        return NumberOfFeatures;
    }

    public int getNumberOfAnnotatedFiles() {
        return NumberOfAnnotatedFiles;
    }

    public double getAvgScatteringDegree() {
        return AvgScatteringDegree;
    }

    public double getAvgLinesOfFeatureCode() {
        return AvgLinesOfFeatureCode;
    }

    public double getAvgNestingDepth() {
        return AvgNestingDepth;
    }
}
