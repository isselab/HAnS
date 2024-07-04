package se.isselab.HAnS.featureExtension.v2;

import se.isselab.HAnS.metrics.v2.ProjectMetrics;

public interface MetricsCallback {
    void onComplete(ProjectMetrics metrics);
}
