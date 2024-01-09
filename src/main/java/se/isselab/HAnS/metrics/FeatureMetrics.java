package se.isselab.HAnS.metrics;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashMap;
import java.util.HashSet;

public class FeatureMetrics {
    private HashMap<String, FeatureFileMapping> fileMapping;
    private HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap;

    //TODO THESIS add scattering degree
    public FeatureMetrics(HashMap<String, FeatureFileMapping> fileMapping, HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap) {
        this.fileMapping = fileMapping;
        this.tanglingMap = tanglingMap;
    }

    public HashMap<String, FeatureFileMapping> getFileMapping() {
        return fileMapping;
    }

    public HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap() {
        return tanglingMap;
    }
}
