package se.isselab.HAnS.featureHistoryView.backgroundTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import git4idea.GitCommit;
import se.isselab.HAnS.featureHistoryView.FeatureHistoryAnalyzer;
import com.intellij.openapi.diagnostic.Logger;
import java.text.SimpleDateFormat;
import java.util.*;

public class CommitExtractionTask extends Task.Backgroundable {
    private final Project project;
    private final CommitExtractionCallback callback;
    private List<GitCommit> commits;  // To hold the extracted commits
    private final Map<String, Set<GitCommit>> featureExistenceMap = new HashMap<>();





    public CommitExtractionTask(Project project, CommitExtractionCallback callback) {
        super(project, "Extracting Commits for Feature Timeline");
        this.project = project;
        this.callback = callback;
    }

    @Override
    public void run(ProgressIndicator indicator) {
        FeatureHistoryAnalyzer analyzer = new FeatureHistoryAnalyzer(project);
        commits = analyzer.extractAllCommits();
//        System.out.println("Number of commits extracted: " + commits.size());

        for (GitCommit commit : commits) {
            List<String> featuresInCommit = analyzer.extractFeaturesFromCommit(commit);

            //map each feature to a set of GitCommit objects where it appears
            for (String feature : featuresInCommit) {
                featureExistenceMap.computeIfAbsent(feature, k -> new HashSet<>()).add(commit);
            }
        }
        System.out.println("Feature existence map: " + featureExistenceMap);

    }

    @Override
    public void onSuccess() {
        callback.onCommitsExtracted(commits, featureExistenceMap);
    }
}
