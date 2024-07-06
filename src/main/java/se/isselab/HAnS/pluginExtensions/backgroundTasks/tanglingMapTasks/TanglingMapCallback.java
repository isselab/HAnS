package se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks;

import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashSet;
import java.util.Map;

/**
 * Callback for the TanglingMap tasks.
 * All methods are optional to implement, and do nothing if not implemented.
 */
public interface TanglingMapCallback {
    default void onComplete(HashSet<FeatureModelFeature> tangledFeatures){}

    default void onComplete(Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap){}
}
