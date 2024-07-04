package se.isselab.HAnS.featureExtension.v2;

import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

public interface MetricsService {

    void getProjectMetricsBackground(MetricsCallback callback);

    boolean isRootFeature(FeatureModelFeature featureName);
}
