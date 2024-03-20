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
    // &begin[NumberOfAnnotatedFiles]
    public static Integer getNumberOfAnnotatedFiles(FullFeatureTree tree, String featureLPQ) {
       int NoAF = countNumberOfAnnotatedFiles(tree, featureLPQ);
       return NoAF;
    }
    // &end[NumberOfAnnotatedFiles]

    /**
     * Retrieves the number of features within the specified item (folder, file) in the project structure tree.
     *
     * @param tree The project structure tree containing the items with features.
     * @param pathToItem The path to the item (folder, file) within the project structure tree.
     * @return An Integer representing the number of features within the specified item. Returns -1 if the item is not found.
     */
    // &begin[NumberOfFeatures]
    public static Integer getNumberOfFeatures(FullFeatureTree tree, String pathToItem) {
        // find corresponding node in the Project Tree
        FullFeatureTree treeNode = findProjectTreeNode(tree, pathToItem);
        // count number of Features
        if (treeNode == null) return -1;
        List<String> features = new ArrayList<>();
        countNumberOfFeaturesInItem(treeNode, features);
        return features.size();
    }
    // &end[NumberOfFeatures]

    // &begin[NestingDepths]
    /**
     * Calculates the maximum nesting depth of a specific feature within the project tree.
     *
     * @param tree The project structure tree.
     * @param feature The LPQ of the feature.
     * @return An Optional Integer containing the maximum nesting depth of the feature, or empty if the feature is not found.
     */
    public static Optional<Integer> getMaxNestingDepth(FullFeatureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().max(Integer::compareTo);
    }

    /**
     * Calculates the minimum nesting depth of a specific feature within the project tree.
     *
     * @param tree The project structure tree.
     * @param feature The LPQ of the feature.
     * @return An Optional Integer containing the minimum nesting depth of the feature, or empty if the feature is not found.
     */
    public static Optional<Integer> getMinNestingDepth(FullFeatureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().min(Integer::compareTo);
    }

    /**
     * Calculates the average nesting depth of a specific feature within the project tree.
     *
     * @param tree The project structure tree.
     * @param feature The LPQ of the feature.
     * @return An Optional Integer containing the average nesting depth of the feature, or empty if the feature is not found.
     */
    public static OptionalDouble getAvgNestingDepth(FullFeatureTree tree, String feature) {
        return getFeatureDepths(tree, feature).stream().mapToDouble(Integer::doubleValue).average();
    }

    private static ArrayList<Integer> getFeatureDepths(FullFeatureTree tree, String targetFeatureLPQ) {
        ArrayList<Integer> occurrences = new ArrayList<>();
        findAndStoreFeatureDepthsRecursive(tree, targetFeatureLPQ, occurrences);
        return occurrences;
    }

    private static void findAndStoreFeatureDepthsRecursive(FullFeatureTree node, String targetFeatureLPQ, ArrayList<Integer> occurrences) {
        if (node.getFeatureList() != null && node.getFeatureList().contains(targetFeatureLPQ)) {
            occurrences.add(node.getDepth());
        }

        if (node.getChildren() != null) {
            for (FullFeatureTree child : node.getChildren()) {
                findAndStoreFeatureDepthsRecursive(child, targetFeatureLPQ, occurrences);
            }
        }
    }
    // &end[NestingDepths]

    private static FullFeatureTree findProjectTreeNode(FullFeatureTree node, String path) {
        if (node != null && node.getPath().equals(path)) {
            return node;
        }

        if (!node.getChildren().isEmpty()) {
            for (FullFeatureTree child : node.getChildren()) {
                FullFeatureTree result = findProjectTreeNode(child, path);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    // &begin[NumberOfFeatures]
    private static void countNumberOfFeaturesInItem(FullFeatureTree node, List<String> features) {
        if (!node.getFeatureList().isEmpty()) {
            features.addAll(node.getFeatureList());
        }
        if (!node.getChildren().isEmpty()) {
            for (FullFeatureTree child : node.getChildren()) {
                countNumberOfFeaturesInItem(child, features);
            }
        }
    }
    // &end[NumberOfFeatures]

    // &begin[NumberOfAnnotatedFiles]
    private static int countNumberOfAnnotatedFiles(FullFeatureTree node, String targetFeatureLPQ) {
        int count = 0;

        if (node.getType() == FullFeatureTree.Type.FILE && node.getFeatureList().contains(targetFeatureLPQ)) {
            count++;
        }

        if (node.getChildren() != null) {
            for (FullFeatureTree child : node.getChildren()) {
                count += countNumberOfAnnotatedFiles(child, targetFeatureLPQ);
            }
        }

        return count;
    }
    // &end[NumberOfAnnotatedFiles]
}
