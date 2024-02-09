package se.isselab.HAnS.vpIntegration;

import java.util.ArrayList;
import java.util.List;

public class FeaturesCodeAnnotations {
    private static FeaturesCodeAnnotations instance = new FeaturesCodeAnnotations();
    private List<String> featureNames;

    private FeaturesCodeAnnotations() {
        featureNames = new ArrayList<>();
    }

    public static FeaturesCodeAnnotations getInstance() {
        return instance;
    }

    public synchronized List<String> getFeatureNames() {
        return new ArrayList<>(featureNames);
    }

    public synchronized void setFeatureNames(List<String> newStrings) {
        this.featureNames = newStrings;
    }
}
