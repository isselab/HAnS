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

package se.isselab.HAnS.featureLocation;

import se.isselab.HAnS.featureLocation.FeatureFileMapping.FileAnnotationKey;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.List;

/**
 * Structure which holds information of all locations of a given feature in a given file
 */
public class FeatureLocation {
    private final String mappedPath;
    private final String mappedBy;
    private final FeatureModelFeature mappedFeature;

    private final FeatureFileMapping.AnnotationType annotationType;

    private final List<FeatureLocationBlock> featureLocations;

    /**
     * Constructor
     *
     * @param mappedPath       Path which should be mapped with the information
     * @param mappedFeature    Feature which should be mapped with the information
     * @param annotationType   AnnotationType of the FeatureLocations
     * @param featureLocations List of FeatureLocationBlocks
     */
    public FeatureLocation(String mappedPath, String mappedBy, FeatureModelFeature mappedFeature, FeatureFileMapping.AnnotationType annotationType, List<FeatureLocationBlock> featureLocations) {
        this.mappedPath = mappedPath;
        this.mappedBy = mappedBy;
        this.mappedFeature = mappedFeature;
        this.annotationType = annotationType;
        this.featureLocations = featureLocations;
    }

    /**
     * Method to get the corresponding path
     *
     * @return Corresponding Path
     */
    public String getMappedPath() {
        return mappedPath;
    }

    /**
     * Method to get the corresponding feature
     *
     * @return Corresponding feature
     */
    public FeatureModelFeature getMappedFeature() {
        return mappedFeature;
    }

    /**
     * Method to get the AnnotationType
     *
     * @return AnnotationType
     */
    public FeatureFileMapping.AnnotationType getAnnotationType() {
        return annotationType;
    }

    /**
     * Method to get all FeatureLocationBlocks for the given feature in the given path
     *
     * @return List of all FeatureLocationBlocks
     */
    public List<FeatureLocationBlock> getFeatureLocations() {
        return featureLocations;
    }

    /**
     * Method to get the path to the file which maps this location
     * @return Path to the file that maps this location
     */
    public String getMappedBy() {
        return mappedBy;
    }

    /**
     * Method to get the file annotation key for this location
     * @return FileAnnotationKey containing file path and originating annotation path
     */
    public FileAnnotationKey getMappedPathPairMappedBy(){
        return new FileAnnotationKey(mappedPath, mappedBy);
    }
}
