package se.isselab.HAnS.vpIntegration;

import java.util.ArrayList;
import java.util.List;

public class FeatureNames {
    private static FeatureNames instance = new FeatureNames();
    private List<String> featureNames;

    private FeatureNames() {
        featureNames = new ArrayList<>();
    }

    public static FeatureNames getInstance() {
        return instance;
    }

    public synchronized List<String> getFeatureNames() {
        return new ArrayList<>(featureNames);
    }

    public synchronized void setFeatureNames(List<String> newStrings) {
        this.featureNames = newStrings;
    }
}
