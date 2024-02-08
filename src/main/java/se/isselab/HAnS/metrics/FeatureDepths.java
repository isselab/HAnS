package se.isselab.HAnS.metrics;

import com.intellij.openapi.project.Project;
import se.isselab.HAnS.ProjectStructureTree6;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.nio.file.Path;
import java.util.*;

public class FeatureDepths {

    public static Integer getNumberOfFeatures(ProjectStructureTree tree, String pathToItem) {
        // find corresponding node in the Project Tree
        ProjectStructureTree treeNode = findProjectTreeNode(tree, pathToItem);
        System.out.println(treeNode.getName());
        System.out.println(treeNode.getPath());
        // count number of Features
        Set<String> features = new HashSet<>();
        countNumberOfFeaturesInItem(treeNode, features);
        System.out.println(Arrays.toString(features.toArray()));
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

    private static ProjectStructureTree  findProjectTreeNode(ProjectStructureTree node, String path) {
        if (node != null && node.getPath().equals(path)) {
            return node;
        }

        if (node.getChildren() != null) {
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
        if (node.getChildren() != null) {
            for (ProjectStructureTree child : node.getChildren()) {
                countNumberOfFeaturesInItem(child, features);
            }
        }

    }
}
