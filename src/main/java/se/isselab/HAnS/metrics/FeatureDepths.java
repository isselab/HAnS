package se.isselab.HAnS.metrics;

import com.intellij.openapi.project.Project;
import se.isselab.HAnS.ProjectStructureTree6;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalDouble;

public class FeatureDepths {

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
}
