package se.isselab.HAnS.featureHistoryView.backgroundTasks;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import git4idea.GitCommit;
import se.isselab.HAnS.featureHistoryView.FeatureHistoryAnalyzer;
import java.util.*;

public class CommitExtractionTask extends Task.Backgroundable {
    private final Project project;
    private final CommitExtractionCallback callback;
    private List<GitCommit> commits;
    private final Map<String, Set<GitCommit>> featureExistenceMap = new HashMap<>();
    private  Map<String, Set<FeatureHistoryAnalyzer.FeatureData>> featureFileMap = new HashMap<>();
    private  Map<String, Set<FeatureHistoryAnalyzer.FeatureData>> featureFolderMap = new HashMap<>();


    public CommitExtractionTask(Project project, CommitExtractionCallback callback) {
        super(project, "Extracting commits for feature timeline");
        this.project = project;
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        FeatureHistoryAnalyzer analyzer = new FeatureHistoryAnalyzer(project);
        commits = analyzer.extractAllCommits();

        // Process commits in parallel
        commits.forEach(commit -> {
            // Handle annotations in commits; the method now doesn't require feature maps as parameters
            List<String> featuresInCommit = analyzer.handleAnnotationsInCommits(commit);

            // Populate featureExistenceMap (for code annotations)
            featuresInCommit.forEach(feature -> featureExistenceMap
                    .computeIfAbsent(feature,  k -> new HashSet<>())
                    .add(commit));
        });

        // Retrieve the feature maps from the analyzer
        featureFileMap = analyzer.getFeatureFileMap();
        featureFolderMap = analyzer.getFeatureFolderMap();


        // Log the feature maps for debugging
        System.out.println("Feature existence map: " + featureExistenceMap);
        //System.out.println("Feature-to-File map: " + featureFileMap);
        //System.out.println("Feature-to-Folder map: " + featureFolderMap);
    }

    @Override
    public void onSuccess() {
        callback.onCommitsExtracted(commits, featureExistenceMap, featureFileMap, featureFolderMap);
    }
}
