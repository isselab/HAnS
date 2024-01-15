package se.isselab.HAnS.featureExtension;

import se.isselab.HAnS.metrics.FeatureMetrics;

public interface HAnSCallback {
    void onComplete(FeatureMetrics metrics);
}
