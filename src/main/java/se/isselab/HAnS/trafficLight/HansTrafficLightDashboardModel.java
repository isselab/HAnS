package se.isselab.HAnS.trafficLight;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import java.util.Map;
import java.util.Set;

public class HansTrafficLightDashboardModel {
    private final boolean isAlive;
    private final int featureCount;
    private final Map<String, Set<String>> filePathsFeatureFileMapping;
    private final Map<String, Set<String>> filePathsFeatureFolderMapping;

    public HansTrafficLightDashboardModel(boolean isAlive, Map<String, Map<String, Set<String>>> result) {
        this.isAlive = isAlive;
        this.featureCount = result.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .mapToInt(Set::size)
                .sum();
        this.filePathsFeatureFileMapping = result.get(FeatureFileMapping.AnnotationType.FILE.toString());
        this.filePathsFeatureFolderMapping = result.get(FeatureFileMapping.AnnotationType.FOLDER.toString());
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int findingsCount() {
        return featureCount;
    }

    public boolean hasFindings() {
        return findingsCount() != 0;
    }

    public Map<String, Set<String>> getFilePathsFeatureFileMapping() {
        return filePathsFeatureFileMapping;
    }

    public Map<String, Set<String>> getFilePathsFeatureFolderMapping() {
        return filePathsFeatureFolderMapping;
    }
}


