/*
Copyright 2024 Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.pluginExtensions.backgroundTasks;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;
import se.isselab.HAnS.metrics.calculators.NestingDepths;
import se.isselab.HAnS.metrics.ProjectMetrics;

public class ProjectMetricsBackgroundTask extends Task.Backgroundable {

    private ProjectMetrics metrics;
    private final MetricsCallback callback;

    /**
     * Background task needs a Callback class that implements IMetricsCallback.
     * It is necessary to callback after succeeding the background task
     *
     * @param project  current Project
     * @param title    Title for Progress Indicator
     * @param callback {@link MetricsCallback} Implementation
     * @param metrics  {@link ProjectMetrics} Implementation
     */
    public ProjectMetricsBackgroundTask(@Nullable Project project, @NotNull String title, MetricsCallback callback, ProjectMetrics metrics) {
        super(project, title);
        this.metrics = metrics;
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        var featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
        var tanglingMap = FeatureTangling.getTanglingMap(super.getProject(), featureFileMappings);
        var nestingDepthMap = NestingDepths.getNestingDepthMap(featureFileMappings);

        // Start the progress indicator
        indicator.setIndeterminate(false);
        indicator.setText("Getting metrics");
        indicator.setFraction(0.0);

        int total = featureFileMappings.values().size();
        int processed = 0;

        for(var featureFileMapping : featureFileMappings.values()){
            if (indicator.isCanceled()) break;

            var feature = featureFileMapping.getFeature();
            var featureLPQ = ReadAction.compute(feature::getLPQText);

            feature.setScatteringDegree(FeatureScattering.getScatteringDegree(featureFileMapping));
            feature.setTanglingDegree(tanglingMap.containsKey(feature)? tanglingMap.get(feature).size() : 0);
            feature.setLineCount(featureFileMapping.getTotalFeatureLineCount());

            feature.setAvgNestingDepth(nestingDepthMap.containsKey(featureLPQ)?
                    nestingDepthMap.get(featureLPQ).stream().mapToInt(p -> p.getSecond()).average().orElse(0.0):
                    0.0);
            feature.setMaxNestingDepth(nestingDepthMap.containsKey(featureLPQ)?
                    nestingDepthMap.get(featureLPQ).stream().mapToInt(p -> p.getSecond()).max().orElse(0):
                    0);
            feature.setMinNestingDepth(nestingDepthMap.containsKey(featureLPQ)? 
                    nestingDepthMap.get(featureLPQ).stream().mapToInt(p -> p.getSecond()).min().orElse(0):
                    0);

            feature.setNumberOfAnnotatedFiles(featureFileMapping.getMappedFilePaths().size());
            feature.setNumberOfFolderAnnotations(featureFileMapping.getFolderAnnotations().size());
            feature.setNumberOfFileAnnotations(featureFileMapping.getFileAnnotations().size());

            // Update the progress
            processed++;
            indicator.setFraction((double) processed / total);
        }
        metrics = new ProjectMetrics(featureFileMappings, tanglingMap, nestingDepthMap);

        // Finish the progress indicator
        indicator.setFraction(1.0);
        indicator.setText("Metrics loaded");
    }

    @Override
    public void onSuccess() {
        callback.onComplete(metrics);
    }
}
