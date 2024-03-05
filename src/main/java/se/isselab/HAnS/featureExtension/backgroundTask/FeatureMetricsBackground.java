/*
Copyright 2024 David Stechow & Philipp Kusmierz

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

package se.isselab.HAnS.featureExtension.backgroundTask;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.HAnSCallback;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureMetrics;
import se.isselab.HAnS.metrics.FeatureTangling;

import java.util.HashMap;
import java.util.HashSet;

public class FeatureMetricsBackground extends BackgroundTask{
    /**
     * Background task needs a Callback class that implements HAnSCallback.
     * It is necessary to callback after succeeding the backgroundtask
     * @param project current Project
     * @param title Title for Progress Indicator
     * @param callback  {@link HAnSCallback} Implementation
     */
    public FeatureMetricsBackground(@Nullable Project project, @NotNull String title, HAnSCallback callback, FeatureMetrics featureMetrics) {
        super(project, title, callback, featureMetrics);
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        HashMap<String, FeatureFileMapping> featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(super.getProject(), featureFileMappings);
        featureMetrics = new FeatureMetrics(featureFileMappings, tanglingMap);
    }
}
