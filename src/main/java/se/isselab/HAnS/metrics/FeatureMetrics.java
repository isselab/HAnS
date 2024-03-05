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

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashMap;
import java.util.HashSet;

public class FeatureMetrics {

    // Initialize all fields of Feature Metrics. Fields will be changed depending on purpose.
    private FeatureFileMapping fileMapping = null;
    private HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = null;
    private HashMap<String, FeatureFileMapping> featureFileMappings = null;
    private FeatureModelFeature feature = null;
    private int tanglingDegree = -1;
    private int scatteringDegree = -1;

    /**
     * Generate empty Feature Metrics for error handling.
     */
    public FeatureMetrics() {

    }

    /**
     * Generate Feature Metrics with Feature
     *
     * @param feature {@link FeatureModelFeature}
     */
    public FeatureMetrics(FeatureModelFeature feature) {
        this.feature = feature;
    }

    /**
     * Generate Feature Metrics with FeatureFileMappings and TanglingMap
     *
     * @param featureFileMappings {@link FeatureFileMapping} of all {@link FeatureModelFeature} represented as HashMap
     * @param tanglingMap         Tangling Map of all {@link FeatureModelFeature} represented as HashMap
     */
    public FeatureMetrics(HashMap<String, FeatureFileMapping> featureFileMappings, HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap) {
        this.tanglingMap = tanglingMap;
        this.featureFileMappings = featureFileMappings;
    }

    /**
     * Generate Feature Metrics with FeatureFileMapping of a Feature
     *
     * @param featureFileMapping {@link FeatureFileMapping} of a {@link FeatureModelFeature}
     */
    public FeatureMetrics(FeatureFileMapping featureFileMapping) {
        this.fileMapping = featureFileMapping;
    }

    /**
     * Generate Feature Metrics with Feature, Tangling Degree and Scattering Degree
     *
     * @param feature          FeatureModelFeature
     * @param tanglingDegree   of feature
     * @param scatteringDegree of feature
     */
    public FeatureMetrics(FeatureModelFeature feature, int tanglingDegree, int scatteringDegree) {
        this.feature = feature;
        this.tanglingDegree = tanglingDegree;
        this.scatteringDegree = scatteringDegree;
    }


    // Getter-Methods

    /**
     * @return FeatureFileMapping of a feature or null if no FeatureFileMapping was calculated
     */
    public FeatureFileMapping getFileMapping() {
        return fileMapping;
    }

    /**
     * @return TanglingMap of the Feature Model or null if no TanglingMap was calculated
     */
    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap() {
        return tanglingMap;
    }

    /**
     * @return FeatureFileMappings of the Feature Model or null if no FeatureFileMappings was calculated
     */
    public HashMap<String, FeatureFileMapping> getFeatureFileMappings() {
        return featureFileMappings;
    }

    /**
     * @return Feature that was assigned to this Feature Metrics
     */
    public FeatureModelFeature getFeature() {
        return feature;
    }

    /**
     * @return Tangling degree of a feature or -1 if no tangling degree was calculated
     */
    public int getTanglingDegree() {
        return tanglingDegree;
    }

    /**
     * @return Scattering degree of a feature or -1 if no scattering degree was calculated
     */
    public int getScatteringDegree() {
        return scatteringDegree;
    }
}
