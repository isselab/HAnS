package se.isselab.HAnS.featureHistoryView;
import com.intellij.openapi.project.Project;
import git4idea.GitCommit;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureHistoryView.backgroundTasks.CommitExtractionTask;
import se.isselab.HAnS.featureHistoryView.backgroundTasks.FeatureExtractionTask;
import java.text.SimpleDateFormat;
import java.util.*;

public class FeatureCommitMapper {
    private final List<String> featureNames;
    private final List<String> commitTimes;
    private final List<String> commitHashes; // For mapping
    private final List<Map<String, Object>> seriesData; // List of maps to hold data points
    private final List<String> deletedFeatureNames;
    private final Map<String, String> deletedFeatureCommits; // Map of deleted features and their last commit times
    private final Map<String, String> deletedFeatureCommitHashes; // Map of deleted features to their last commit hash
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd HH:mm:ss yyyy"); // Date formatter for human-readable format

    public FeatureCommitMapper() {
        this.featureNames = new ArrayList<>();
        this.commitTimes = new ArrayList<>();
        this.commitHashes = new ArrayList<>();
        this.seriesData = new ArrayList<>();
        this.deletedFeatureNames = new ArrayList<>();
        this.deletedFeatureCommits = new HashMap<>();
        this.deletedFeatureCommitHashes = new HashMap<>();
    }

    public void mapFeaturesAndCommits(Project project, Runnable onComplete) {
        // First, extract the features in the background
        FeatureExtractionTask featureTask = new FeatureExtractionTask(project, allFeatures -> {
            this.featureNames.addAll(allFeatures);
            //System.out.println("Features extracted: " + featureNames);
            // Once features are extracted, proceed to extract commits
            CommitExtractionTask commitTask = new CommitExtractionTask(project, (commits, featureExistenceMap, featureFileMap, featureFolderMap) -> {
                commits.forEach(commit -> {
                    commitHashes.add(commit.getId().asString());
                    commitTimes.add(dateFormatter.format(new Date(commit.getCommitTime())));
                });
                identifyDeletedFeatures(featureExistenceMap);
                createSeriesData(featureExistenceMap, featureFileMap, featureFolderMap);
                //System.out.println("FeatureCommitMapper - featureFileMap: " + featureFileMap);
                //System.out.println("FeatureCommitMapper - featureFolderMap: " + featureFolderMap);
                // Once everything is done, call the onComplete callback
                if (onComplete != null) {
                    onComplete.run();
                }
            });
            commitTask.queue();
        });
        featureTask.queue();
    }

    // Method to identify deleted features by comparing extracted features with the current model
    private void identifyDeletedFeatures(@NotNull Map<String, Set<GitCommit>> featureExistenceMap) {
        featureExistenceMap.keySet().stream()
                .filter(feature -> !featureNames.contains(feature))
                .forEach(feature -> {
                    deletedFeatureNames.add(feature);
                    // Get the last commit for the deleted feature
                    featureExistenceMap.get(feature).stream()
                            .max(Comparator.comparingLong(GitCommit::getCommitTime))
                            .ifPresent(commit -> {
                                String formattedTime = dateFormatter.format(new Date(commit.getCommitTime()));
                                deletedFeatureCommits.put(feature, formattedTime);
                                deletedFeatureCommitHashes.put(feature, commit.getId().asString());
                            });
                });
    }

    private void createSeriesData(Map<String, Set<GitCommit>> featureExistenceMap,
                                  Map<String, Set<FeatureHistoryAnalyzer.FeatureData>> featureFileMap,
                                  Map<String, Set<FeatureHistoryAnalyzer.FeatureData>> featureFolderMap) {
        // Map commit hashes to indices
        Map<String, Integer> commitHashToIndex = mapCommitHashesToIndices();

        // Map feature names to indices
        Map<String, Integer> featureNameToIndex = mapFeatureNamesToIndices();

        // Process code annotations
        processCodeAnnotations(featureExistenceMap, commitHashToIndex, featureNameToIndex);

        // Process feature-to-file mappings
        processFeatureFileMappings(featureFileMap, commitHashToIndex, featureNameToIndex);

        // Process feature-to-folder mappings
        processFeatureFolderMappings(featureFolderMap, commitHashToIndex, featureNameToIndex);
    }

    private Map<String, Integer> mapCommitHashesToIndices() {
        Map<String, Integer> commitHashToIndex = new HashMap<>();
        for (int i = 0; i < commitHashes.size(); i++) {
            commitHashToIndex.put(commitHashes.get(i), i);
        }
        return commitHashToIndex;
    }

    private Map<String, Integer> mapFeatureNamesToIndices() {
        Map<String, Integer> featureNameToIndex = new HashMap<>();
        for (int i = 0; i < featureNames.size(); i++) {
            featureNameToIndex.put(featureNames.get(i), i);
        }
        return featureNameToIndex;
    }

    private void processCodeAnnotations(Map<String, Set<GitCommit>> featureExistenceMap,
                                        Map<String, Integer> commitHashToIndex,
                                        Map<String, Integer> featureNameToIndex) {
        for (Map.Entry<String, Set<GitCommit>> entry : featureExistenceMap.entrySet()) {
            String featureName = entry.getKey();
            int featureIndex = featureNameToIndex.getOrDefault(featureName, -1);
            if (featureIndex == -1) {
                continue; // Feature not found
            }
            Set<GitCommit> commitsForFeature = entry.getValue();
            for (GitCommit commit : commitsForFeature) {
                String commitHash = commit.getId().asString();
                Integer commitIndex = commitHashToIndex.get(commitHash);

                if (commitIndex != null) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("featureIndex", featureIndex);
                    dataPoint.put("commitIndex", commitIndex);
                    dataPoint.put("commitHash", commitHash);
                    dataPoint.put("commitMessage", commit.getFullMessage());
                    dataPoint.put("commitAuthor", commit.getAuthor().getName());
                    dataPoint.put("type", "codeAnnotation");
                    seriesData.add(dataPoint);
                }
            }
        }
    }

    private void processFeatureFileMappings(Map<String, Set<FeatureHistoryAnalyzer.FeatureData>> featureFileMap,
                                            Map<String, Integer> commitHashToIndex,
                                            Map<String, Integer> featureNameToIndex) {
        for (Map.Entry<String, Set<FeatureHistoryAnalyzer.FeatureData>> entry : featureFileMap.entrySet()) {
            String featureName = normalizeFeatureName(entry.getKey());
            int featureIndex = featureNameToIndex.getOrDefault(featureName, -1);

            if (featureIndex == -1) {
                continue; // Feature not found
            }
            Set<FeatureHistoryAnalyzer.FeatureData> dataSet = entry.getValue();
            for (FeatureHistoryAnalyzer.FeatureData data : dataSet) {
                String commitHash = data.getCommitHash();
                Integer commitIndex = commitHashToIndex.get(commitHash);
                if (commitIndex != null) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("featureIndex", featureIndex);
                    dataPoint.put("commitIndex", commitIndex);
                    dataPoint.put("commitHash", commitHash);
                    dataPoint.put("commitTime", data.getCommitTime());
                    dataPoint.put("entityName", data.getEntityName()); // File name
                    dataPoint.put("type", "fileMapping");
                    seriesData.add(dataPoint);
                }
            }
        }
    }

    private void processFeatureFolderMappings(Map<String, Set<FeatureHistoryAnalyzer.FeatureData>> featureFolderMap,
                                              Map<String, Integer> commitHashToIndex,
                                              Map<String, Integer> featureNameToIndex) {
        for (Map.Entry<String, Set<FeatureHistoryAnalyzer.FeatureData>> entry : featureFolderMap.entrySet()) {
            String featureName = normalizeFeatureName(entry.getKey());
            int featureIndex = featureNameToIndex.getOrDefault(featureName, -1);
            if (featureIndex == -1) {
                continue; // Feature not found
            }
            Set<FeatureHistoryAnalyzer.FeatureData> dataSet = entry.getValue();
            for (FeatureHistoryAnalyzer.FeatureData data : dataSet) {
                String commitHash = data.getCommitHash();
                Integer commitIndex = commitHashToIndex.get(commitHash);
                if (commitIndex != null) {
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("featureIndex", featureIndex);
                    dataPoint.put("commitIndex", commitIndex);
                    dataPoint.put("commitHash", commitHash);
                    dataPoint.put("commitTime", data.getCommitTime());
                    dataPoint.put("entityName", data.getEntityName()); // Folder name
                    dataPoint.put("type", "folderMapping");
                    seriesData.add(dataPoint);
                }
            }
        }
    }
    private String normalizeFeatureName(String featureName) {
        return featureName.trim().replaceAll(",", "");
    }

    // Getters for the chart data
    public List<String> getFeatureNames() {
       // System.out.println("Returning feature names: " + featureNames);
        return featureNames;
    }

    public List<String> getCommitTimes() {
       // System.out.println("Returning commit times: " + commitTimes);
        return commitTimes;
    }

    public List<Map<String, Object>> getSeriesData() {
        System.out.println("Series Data: " + seriesData);
        return seriesData;
    }

    // Getter for commitHashes
    public List<String> getCommitHashes() {
        return commitHashes;
    }

    // Getters for the deleted features and their commit times
    public List<String> getDeletedFeatureNames() {
        return deletedFeatureNames;
    }

    public Map<String, String> getDeletedFeatureCommits() {
        return deletedFeatureCommits;
    }

    // Add getter for deletedFeatureCommitHashes
    public Map<String, String> getDeletedFeatureCommitHashes() {
        return deletedFeatureCommitHashes;
    }
}