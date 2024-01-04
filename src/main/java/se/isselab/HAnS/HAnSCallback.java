package se.isselab.HAnS;

import se.isselab.HAnS.metrics.FeatureMetrics;

public interface HAnSCallback {
    void onComplete(FeatureMetrics metrics);
}
