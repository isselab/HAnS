package se.isselab.HAnS.metrics;
import java.util.*;

public class NestingDepths {

    /**
     * Calculates the number of file annotations directly referencing the specified feature.
     *
     * @param tree The project structure tree containing file annotations.
     * @param featureLPQ The feature's LPQ used to identify it.
     * @return An Integer representing the total number of files annotated with the specified feature.
     */
    public static Integer getNumberOfAnnotatedFiles(ProjectStructureTree tree, String featureLPQ) {
       int NoAF = countNumberOfAnnotatedFiles(tree, featureLPQ);
       return NoAF;
    }

    /**
     * Retrieves the number of features within the specified item (folder, file) in the project structure tree.
     *
     * @param tree The project structure tree containing the items with features.
     * @param pathToItem The path to the item (folder, file) within the project structure tree.
     * @return An Integer representing the number of features within the specified item. Returns -1 if the item is not found.
     */
    public static Integer getNumberOfFeatures(ProjectStructureTree tree, String pathToItem) {
        // find corresponding node in the Project Tree
        ProjectStructureTree treeNode = findProjectTreeNode(tree, pathToItem);
        // count number of Features
        if (treeNode == null) return -1;
        Set<String> features = new HashSet<>();
        countNumberOfFeaturesInItem(treeNode, features);
        return features.size();
    }

    /**
     * Calculates the maximum nesting depth of a specific feature within the project tree.
     *
     * @param tree The project structure tree.
     * @param feature The LPQ of the feature.
     * @return An Optional Integer containing the maximum nesting depth of the feature, or empty if the feature is not found.
     */
    public static Optional<Integer> getMaxNestingDepth(ProjectStructureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().max(Integer::compareTo);
    }

    /**
     * Calculates the minimum nesting depth of a specific feature within the project tree.
     *
     * @param tree The project structure tree.
     * @param feature The LPQ of the feature.
     * @return An Optional Integer containing the minimum nesting depth of the feature, or empty if the feature is not found.
     */
    public static Optional<Integer> getMinNestingDepth(ProjectStructureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().min(Integer::compareTo);
    }

    /**
     * Calculates the average nesting depth of a specific feature within the project tree.
     *
     * @param tree The project structure tree.
     * @param feature The LPQ of the feature.
     * @return An Optional Integer containing the average nesting depth of the feature, or empty if the feature is not found.
     */
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

    private static ProjectStructureTree findProjectTreeNode(ProjectStructureTree node, String path) {
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
