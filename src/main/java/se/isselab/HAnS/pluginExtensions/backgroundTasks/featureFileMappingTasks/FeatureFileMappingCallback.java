package se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import java.util.Map;

/**
 * Callback for the FeatureFileMapping tasks.
 * All methods are optional to implement, and do nothing if not implemented.
 */
public interface FeatureFileMappingCallback {

    default void onComplete(FeatureFileMapping featureFileMapping){}

    default void onComplete(Map<String, FeatureFileMapping> featureFileMappings){}
}
