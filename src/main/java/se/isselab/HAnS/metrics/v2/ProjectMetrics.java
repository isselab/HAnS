package se.isselab.HAnS.metrics.v2;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashMap;
import java.util.HashSet;

public class ProjectMetrics {

    private HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = null; // The tangled features of entire project
    private HashMap<String, FeatureFileMapping> featureFileMappings = null; // The FeatureFileMappings of entire project, string = path and FeatureFileMapping =
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
     */
    public ProjectMetrics(HashMap<String, FeatureFileMapping> featureFileMappings, HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap) {
        this.tanglingMap = tanglingMap;
        this.featureFileMappings = featureFileMappings;
        this.NumberOfFeatures = featureFileMappings.size();

        var featLocations = new HashSet<String>();
        featureFileMappings.values().stream().map(FeatureFileMapping::getMappedFilePaths)
                .forEach(featLocations::addAll);
        this.NumberOfAnnotatedFiles = featLocations.size();

        AvgScatteringDegree = featureFileMappings.values().stream()
                .map(FeatureFileMapping::getFeature)
                .mapToInt(FeatureModelFeature::getScatteringDegree)
                .average().orElse(0);

        AvgLinesOfFeatureCode = featureFileMappings.values().stream()
                .map(FeatureFileMapping::getFeature)
                .mapToInt(FeatureModelFeature::getLineCount)
                .average().orElse(0);

        AvgNestingDepth = 0.0; //TODO: Create correct implementation
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
