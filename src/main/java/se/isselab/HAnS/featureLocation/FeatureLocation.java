package se.isselab.HAnS.featureLocation;

import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.List;

public class FeatureLocation {
    private final String mappedPath;
    private final FeatureModelFeature mappedFeature;

    private final FeatureFileMapping.AnnotationType annotationType;

    private List<FeatureLocationBlock> featureLocations;

    public FeatureLocation(String mappedPath, FeatureModelFeature mappedFeature, FeatureFileMapping.AnnotationType annotationType, List<FeatureLocationBlock> featureLocations) {
        this.mappedPath = mappedPath;
        this.mappedFeature = mappedFeature;
        this.annotationType = annotationType;
        this.featureLocations = featureLocations;
    }

    public String getMappedPath() {
        return mappedPath;
    }

    public FeatureModelFeature getMappedFeature() {
        return mappedFeature;
    }

    public FeatureFileMapping.AnnotationType getAnnotationType() {
        return annotationType;
    }

    public List<FeatureLocationBlock> getFeatureLocations() {
        return featureLocations;
    }
}
