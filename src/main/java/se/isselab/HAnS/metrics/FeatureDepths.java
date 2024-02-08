package se.isselab.HAnS.metrics;
import com.intellij.util.io.Decompressor;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import java.util.*;

public class FeatureDepths {

    // NoAF: Calculate Number of File Annotations: total number of file
    // annotations directly referencing the feature
    public static Integer getNumberOfAnnotatedFiles(ProjectStructureTree tree, String featureLPQ) {
       int NoAF = countNumberOfAnnotatedFiles(tree, featureLPQ);
       return NoAF;
    }

    public static Integer getNumberOfFeatures(ProjectStructureTree tree, String pathToItem) {
        // find corresponding node in the Project Tree
        ProjectStructureTree treeNode = findProjectTreeNode(tree, pathToItem);
        // count number of Features
        if (treeNode == null) return -1;
        Set<String> features = new HashSet<>();
        countNumberOfFeaturesInItem(treeNode, features);
        return features.size();
    }

    // takes project tree and feature LPQ as parameter
    // return max nesting depth of specific feature
    public static Optional<Integer> getMaxNestingDepth(ProjectStructureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().max(Integer::compareTo);
    }

    public static Optional<Integer> getMinNestingDepth(ProjectStructureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().min(Integer::compareTo);
    }

    public static OptionalDouble getAvgNestingDepth(ProjectStructureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().mapToDouble(Integer::doubleValue).average();
    }
    private static ArrayList<Integer> getFeatureDepths(ProjectStructureTree tree, String targetFeatureLPQ) {
        ArrayList<Integer> occurrences = new ArrayList<>();
        findAndStoreFeatureDepthsRecursive(tree, targetFeatureLPQ, occurrences);
        return occurrences;
    }

    private static void findAndStoreFeatureDepthsRecursive(ProjectStructureTree node, String targetFeatureLPQ, ArrayList<Integer> occurrences) {
        if (node.getFeatureList() != null && node.getFeatureList().contains(targetFeatureLPQ)) {
            occurrences.add(node.getDepth());
        }

        if (node.getChildren() != null) {
            for (ProjectStructureTree child : node.getChildren()) {
                findAndStoreFeatureDepthsRecursive(child, targetFeatureLPQ, occurrences);
            }
        }
    }

    public static ProjectStructureTree  findProjectTreeNode(ProjectStructureTree node, String path) {
        if (node != null && node.getPath().equals(path)) {
            return node;
        }

        if (!node.getChildren().isEmpty()) {
            for (ProjectStructureTree child : node.getChildren()) {
                ProjectStructureTree result = findProjectTreeNode(child, path);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static void countNumberOfFeaturesInItem(ProjectStructureTree node, Set<String> features) {
        if (!node.getFeatureList().isEmpty()) {
            features.addAll(node.getFeatureList());
        }
        if (!node.getChildren().isEmpty()) {
            for (ProjectStructureTree child : node.getChildren()) {
                countNumberOfFeaturesInItem(child, features);
            }
        }

    }

    private static int countNumberOfAnnotatedFiles(ProjectStructureTree node, String targetFeatureLPQ) {
        int count = 0;

        if (node.getType() == ProjectStructureTree.Type.FILE && node.getFeatureList().contains(targetFeatureLPQ)) {
            count++;
        }

        if (node.getChildren() != null) {
            for (ProjectStructureTree child : node.getChildren()) {
                count += countNumberOfAnnotatedFiles(child, targetFeatureLPQ);
            }
        }

        return count;
    }
}
