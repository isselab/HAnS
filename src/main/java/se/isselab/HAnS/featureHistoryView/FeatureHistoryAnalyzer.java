package se.isselab.HAnS.featureHistoryView;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.Hash;
import git4idea.GitCommit;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandler;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryManager;



import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FeatureHistoryAnalyzer {

    private final Project project;

    public FeatureHistoryAnalyzer(Project project) {
        this.project = project;
    }

    // Method to extract all commits
    public List<GitCommit> extractAllCommits() {
        List<GitCommit> allCommits = new ArrayList<>();
        GitRepositoryManager repositoryManager = GitRepositoryManager.getInstance(project);

        for (GitRepository gitRepository : repositoryManager.getRepositories()) {
            VirtualFile rootDir = gitRepository.getRoot();

            try {
                List<GitCommit> commits = GitHistoryUtils.history(project, rootDir);
                allCommits.addAll(commits);
            } catch (VcsException e) {
                e.printStackTrace();
            }
        }

        allCommits.sort(Comparator.comparing(this::getCommitterDate));
        return allCommits;
    }

    // Method to extract feature annotations from a commit
    public List<String> extractFeaturesFromCommit(GitCommit commit) {
        // Use a map to track feature names (case-insensitive key) and prefer names with an uppercase first letter
        Map<String, String> featureMap = new HashMap<>();

        GitRepositoryManager repositoryManager = GitRepositoryManager.getInstance(project);

        for (GitRepository gitRepository : repositoryManager.getRepositories()) {
            VirtualFile rootDir = gitRepository.getRoot();

            try {
                List<String> diffs = getCommitDiffs(project, rootDir, commit);

                for (String diff : diffs) {
                    String[] lines = diff.split("\\R");  // Split by newlines

                    for (String line : lines) {
                        // Extract multiple feature names from the line if applicable
                        List<String> featureNames = extractFeatureNamesFromLine(line);

                        for (String featureName : featureNames) {
                            // If we already have a feature in the map, check for uppercase preference
                            String lowerCaseFeature = featureName.toLowerCase();
                            if (!featureMap.containsKey(lowerCaseFeature) || Character.isUpperCase(featureName.charAt(0))) {
                                // Store the feature with priority for uppercase first letter
                                featureMap.put(lowerCaseFeature, featureName);
                            }
                        }
                    }
                }

            } catch (VcsException e) {
                e.printStackTrace();
            }
        }

        // Return the final list of preferred feature names
        return new ArrayList<>(featureMap.values());
    }


    private List<String> getCommitDiffs(Project project, VirtualFile root, GitCommit commit) throws VcsException {
        List<String> diffs = new ArrayList<>();
        String commitHash = commit.getId().asString();

        for (Hash parentHash : commit.getParents()) {
            String parentHashString = parentHash.asString(); // Convert Hash to string
            GitLineHandler handler = new GitLineHandler(project, root, GitCommand.DIFF);
            handler.addParameters(parentHashString, commitHash);
            handler.setSilent(true);

            GitCommandResult result = Git.getInstance().runCommand(handler);
            if (result.success()) {
                String diffOutput = result.getOutputOrThrow();
                if (!StringUtil.isEmptyOrSpaces(diffOutput)) {
                    diffs.add(diffOutput);
                }
            } else {
                throw new VcsException("Failed to get diff for commit: " + commitHash);
            }
        }

        return diffs;
    }

    // Method to extract feature names from a line using regex
    private List<String> extractFeatureNamesFromLine(String line) {
        List<String> features = new ArrayList<>();

        // Define the regex pattern
        Pattern pattern = Pattern.compile("&(?:begin|end|line)\\[([^\\]]+)\\]");
        Matcher matcher = pattern.matcher(line);

        // Find all matches of the pattern in the line
        while (matcher.find()) {
            // Extract the content inside the brackets (group 1)
            String insideBrackets = matcher.group(1);

            // Split by comma in case there are multiple features
            String[] featureArray = insideBrackets.split(",");

            // Trim and add each feature to the list
            for (String feature : featureArray) {
                features.add(feature.trim());
            }
        }

        return features;
    }

    public Date getCommitterDate(GitCommit commit) {
        return new Date(commit.getCommitTime());
    }
}