package se.isselab.HAnS.featureHistoryView.backgroundTasks;

import java.util.List;

public interface FeatureExtractionCallback {
    void onFeaturesExtracted(List<String> features);
}