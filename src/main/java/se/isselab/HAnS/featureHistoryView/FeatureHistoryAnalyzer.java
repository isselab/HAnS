package se.isselab.HAnS.featureHistoryView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.Hash;
import git4idea.GitCommit;
import git4idea.commands.Git;
import git4idea.commands.GitCommand;
import git4idea.commands.GitCommandResult;
import git4idea.commands.GitLineHandler;
import git4idea.history.GitHistoryUtils;
import git4idea.repo.GitRepositoryManager;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class FeatureHistoryAnalyzer {
    private final Project project;
    public FeatureHistoryAnalyzer(Project project) {
        this.project = project;
    }
    private static final Pattern FEATURE_PATTERN = Pattern.compile("&(?:begin|end|line)\\[([^]]+)]");
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd HH:mm:ss yyyy");
    private final Map<String, Set<FeatureData>> featureFileMap = new HashMap<>();
    private final Map<String, Set<FeatureData>> featureFolderMap = new HashMap<>();
    public static class FeatureData {
        private final String entityName;  // File or folder name
        private final String commitHash;
        private final String commitTime;
        public FeatureData(String entityName, String commitHash, String commitTime) {
            this.entityName = entityName;
            this.commitHash = commitHash;
            this.commitTime = commitTime;
        }
        public String getEntityName() {
            return entityName;
        }
        public String getCommitHash() {
            return commitHash;
        }
        public String getCommitTime() {
            return commitTime;
        }
        @Override
        public String toString() {
            return String.format("Entity: %s, Commit: %s, Time: %s", entityName, commitHash, commitTime);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FeatureData that = (FeatureData) o;

            return entityName.equals(that.entityName);
        }
        @Override
        public int hashCode() {
            return entityName.hashCode();
        }
    }

    // Method to extract all commits
    public List<GitCommit> extractAllCommits() {
        GitRepositoryManager repositoryManager = GitRepositoryManager.getInstance(project);
        return repositoryManager.getRepositories()
                .stream()
                .map(repo -> {
                    try {
                        return GitHistoryUtils.history(project, repo.getRoot());
                    } catch (VcsException e) {
                        e.printStackTrace();
                        return Collections.<GitCommit>emptyList(); // Return empty list in case of failure
                    }
                })
                .flatMap(Collection::stream)
                // Sort commits from older to newer
                .sorted(Comparator.comparingLong(GitCommit::getCommitTime))
                .collect(Collectors.toList());
    }

    // Main method to handle annotations in commits
    public List<String> handleAnnotationsInCommits(GitCommit commit) {
        Map<String, String> featureMap = new HashMap<>();
        String commitHash = commit.getId().asString();
        String commitTime = dateFormatter.format(new Date(commit.getCommitTime()));
        GitRepositoryManager repositoryManager = GitRepositoryManager.getInstance(project);
        repositoryManager.getRepositories().forEach(repo -> {
            VirtualFile rootDir = repo.getRoot();
            try {
                // Process code annotations
                processCodeAnnotations(commit, rootDir, featureMap);
                // Handle feature-to-file and feature-to-folder annotations
                handleFeatureAnnotationsInCommit(commit, commitHash, commitTime);
            } catch (VcsException e) {
                e.printStackTrace();
            }
        });
        return new ArrayList<>(featureMap.values());
    }

    // Process code annotations from diffs
    private void processCodeAnnotations(GitCommit commit, VirtualFile rootDir, Map<String, String> featureMap) throws VcsException {
        List<String> diffs = getCommitDiffs(project, rootDir, commit);
        diffs.stream()
                .flatMap(diff -> Arrays.stream(diff.split("\\R"))) // Split by new lines
                .filter(line -> line.startsWith("+") || line.startsWith("-")) // Filter added/modified lines
                .forEach(line -> extractFeatureNamesFromLine(line)
                        .forEach(featureName -> featureMap.merge(
                                featureName.toLowerCase(),
                                featureName,
                                (oldValue, newValue) -> Character.isUpperCase(newValue.charAt(0)) ? newValue : oldValue
                        ))
                );
    }

    // Handle feature-to-file and feature-to-folder annotations in a commit
    private void handleFeatureAnnotationsInCommit(GitCommit commit, String commitHash, String commitTime) throws VcsException {
        List<String> annotationFiles = getAnnotationFilesAtCommit(commit);
        for (String annotationFile : annotationFiles) {
            // Check file type first before getting file content
            if (annotationFile.endsWith(".feature-to-file") || annotationFile.endsWith(".feature-to-folder")) {
                String fileContent = getFileContentAtCommit(commit, annotationFile);  // Retrieve content only when necessary
                if (fileContent != null) {
                    if (annotationFile.endsWith(".feature-to-file")) {
                        parseFeatureToFileContent(fileContent, annotationFile, commitHash, commitTime);
                    } else if (annotationFile.endsWith(".feature-to-folder")) {
                        parseFeatureToFolderContent(fileContent, annotationFile, commitHash, commitTime);
                    }
                }
            }
        }
    }

    // Get list of .feature-to-file and .feature-to-folder files changed in the commit
    private List<String> getAnnotationFilesAtCommit(GitCommit commit) throws VcsException {
        List<String> annotationFiles = new ArrayList<>();
        GitLineHandler handler = new GitLineHandler(project, commit.getRoot(), GitCommand.SHOW);
        handler.addParameters("--name-only", "--pretty=", commit.getId().asString());
        handler.setSilent(true);
        handler.setStdoutSuppressed(false);
        handler.setStderrSuppressed(true);

        GitCommandResult result = Git.getInstance().runCommand(handler);
        if (result.success()) {
            List<String> output = result.getOutput();
            for (String line : output) {
                if (line.endsWith(".feature-to-file") || line.endsWith(".feature-to-folder")) {
                    annotationFiles.add(line);
                }
            }
        } else {
            System.err.println("Failed to list files at commit: " + commit.getId().asString());
        }
        return annotationFiles;
    }

    // Get content of a file at a specific commit
    private String getFileContentAtCommit(GitCommit commit, String filePath) {
        try {
            GitLineHandler handler = new GitLineHandler(project, commit.getRoot(), GitCommand.SHOW);
            String commitHash = commit.getId().asString();
            handler.addParameters(commitHash + ":" + filePath);
            handler.setSilent(true);
            handler.setStdoutSuppressed(false);
            handler.setStderrSuppressed(true);

            GitCommandResult result = Git.getInstance().runCommand(handler);
            if (result.success()) {
                List<String> output = result.getOutput();
                return String.join("\n", output);
            } else {
                // Log the error
                System.err.println("Failed to get content of file " + filePath + " at commit " + commitHash);
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception while getting file content: " + e.getMessage());
            return null;
        }
    }

    // Parse feature-to-file content and update featureFileMap
    private void parseFeatureToFileContent(String content, String filePath, String commitHash, String commitTime) {
        List<String> lines = Arrays.asList(content.split("\\R"));
        for (int i = 0; i < lines.size() - 1; i += 2) {
            String fileLine = lines.get(i).trim();
            String featuresLine = lines.get(i + 1).trim();

            if (fileLine.isEmpty() || featuresLine.isEmpty()) continue;

            String[] files = fileLine.split("\\s+");
            String[] features = featuresLine.split("\\s+");

            for (String feature : features) {
                featureFileMap.computeIfAbsent(feature, k -> new HashSet<>());
                Set<FeatureData> dataSet = featureFileMap.get(feature);
                for (String file : files) {
                    FeatureData newData = new FeatureData(file, commitHash, commitTime);
                    dataSet.add(newData); // Set will automatically prevent duplicates
                }
            }
        }
    }

    // Parse feature-to-folder content and update featureFolderMap
    private void parseFeatureToFolderContent(String content, String filePath, String commitHash, String commitTime) {
        String[] features = content.trim().split("\\s+");

        // Extract folder name from file path
        String folderPath = filePath.substring(0, filePath.lastIndexOf('/'));
        String folderName = folderPath.substring(folderPath.lastIndexOf('/') + 1);

        for (String feature : features) {
            featureFolderMap.computeIfAbsent(feature, k -> new HashSet<>());
            Set<FeatureData> dataSet = featureFolderMap.get(feature);
            FeatureData newData = new FeatureData(folderName, commitHash, commitTime);
            dataSet.add(newData); // Set will automatically prevent duplicates
        }
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
        Matcher matcher = FEATURE_PATTERN.matcher(line);
        while (matcher.find()) {
            String[] featureArray = matcher.group(1).split(",");
            Arrays.stream(featureArray)
                    .map(String::trim)
                    .forEach(features::add);
        }
        return features;
    }
    public Map<String, Set<FeatureData>> getFeatureFileMap() {
        return featureFileMap;
    }
    public Map<String, Set<FeatureData>> getFeatureFolderMap() {
        return featureFolderMap;
    }

}
