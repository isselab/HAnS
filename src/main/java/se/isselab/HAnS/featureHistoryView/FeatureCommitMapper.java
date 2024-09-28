package se.isselab.HAnS.featureHistoryView;

import com.intellij.openapi.project.Project;
import git4idea.GitCommit;
import se.isselab.HAnS.featureHistoryView.backgroundTasks.CommitExtractionTask;
import se.isselab.HAnS.featureHistoryView.backgroundTasks.FeatureExtractionTask;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FeatureCommitMapper {

    private final List<String> featureNames;
    private final List<String> commitTimes;
    private final List<String> commitHashes; // For mapping
    private final List<Map<String, Object>> seriesData; // List of maps to hold data points
    private final List<String> deletedFeatureNames;
    private final Map<String, String> deletedFeatureCommits; // Map of deleted features and their last commit times
    private final Map<String, String> deletedFeatureCommitHashes; // Map of deleted features to their last commit hash
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy"); // Date formatter for human-readable format

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
            this.featureNames.addAll(allFeatures);  // Collect feature names
            //System.out.println("Features extracted: " + featureNames);  // Print the extracted feature names

            // Once features are extracted, proceed to extract commits
            CommitExtractionTask commitTask = new CommitExtractionTask(project, (commits, featureExistenceMap) -> {
                // Collect commit hashes and labels
                for (GitCommit commit : commits) {
                    String commitHash = commit.getId().asString();
                    commitHashes.add(commitHash);
                    String formattedTime = dateFormatter.format(new Date(commit.getCommitTime()));
                    commitTimes.add(formattedTime);
                }

                identifyDeletedFeatures(featureExistenceMap);
                createSeriesData(featureExistenceMap);

                // Once everything is done, call the onComplete callback
                if (onComplete != null) {
                    onComplete.run();
                }

            });

            commitTask.queue(); // Queue the commit extraction task

        });

        featureTask.queue(); // Queue the feature extraction task
    }

    private void identifyDeletedFeatures(Map<String, Set<GitCommit>> featureExistenceMap) {
        for (String feature : featureExistenceMap.keySet()) {
            if (!featureNames.contains(feature)) {
                deletedFeatureNames.add(feature);
                // Get the last commit for the deleted feature
                Optional<GitCommit> lastCommit = featureExistenceMap.get(feature).stream()
                        .max(Comparator.comparingLong(GitCommit::getCommitTime));

                if (lastCommit.isPresent()) {
                    GitCommit commit = lastCommit.get();
                    String formattedTime = dateFormatter.format(new Date(commit.getCommitTime()));
                    deletedFeatureCommits.put(feature, formattedTime);
                    deletedFeatureCommitHashes.put(feature, commit.getId().asString());
                }
            }
        }
    }

    private void createSeriesData(Map<String, Set<GitCommit>> featureExistenceMap) {
        // Map commit hashes to indices
        Map<String, Integer> commitHashToIndex = new HashMap<>();
        for (int i = 0; i < commitHashes.size(); i++) {
            commitHashToIndex.put(commitHashes.get(i), i);
        }

        // Loop over all feature names and map data
        for (int featureIndex = 0; featureIndex < featureNames.size(); featureIndex++) {
            String feature = featureNames.get(featureIndex);

            Set<GitCommit> commitsForFeature = featureExistenceMap.getOrDefault(feature, new HashSet<>());

            for (GitCommit commit : commitsForFeature) {
                String commitHash = commit.getId().asString();
                Integer commitIndex = commitHashToIndex.get(commitHash);

                if (commitIndex != null) {
                    // Create a map to represent a data point
                    Map<String, Object> dataPoint = new HashMap<>();
                    dataPoint.put("featureIndex", featureIndex);
                    dataPoint.put("commitIndex", commitIndex);
                    dataPoint.put("commitHash", commitHash);

                    seriesData.add(dataPoint);
                }
            }
        }
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