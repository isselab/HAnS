package se.isselab.HAnS.featureLocation;

import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.List;

/**
 * Structure which holds information of all locations of a given feature in a given file
 */
public class FeatureLocation {
    private final String mappedPath;
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
    public FeatureLocation(String mappedPath, FeatureModelFeature mappedFeature, FeatureFileMapping.AnnotationType annotationType, List<FeatureLocationBlock> featureLocations) {
        this.mappedPath = mappedPath;
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
}
