package se.isselab.HAnS.featureHistoryView.backgroundTasks;

import git4idea.GitCommit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CommitExtractionCallback {

        void onCommitsExtracted(List<GitCommit> commits, Map<String, Set<GitCommit>> featureExistenceMap);
    }