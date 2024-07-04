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
package se.isselab.HAnS.pluginExtensions.backgroundTasks.featureBackgroundTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.NestingDepths;

public class GetNestingDepthsBackgroundTask extends Task.Backgroundable {

    private final FeatureModelFeature feature;
    private final FeatureCallback callback;

    public GetNestingDepthsBackgroundTask(@Nullable Project project, @NlsContexts.ProgressTitle @NotNull String title,
                                          FeatureCallback callback, FeatureModelFeature feature) {
        super(project, title);
        this.callback = callback;
        this.feature = feature;

    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        var nestingDepths = NestingDepths.getFeatureNestingDepths(super.getProject() ,feature);
        if (nestingDepths == null) {
            return;
        }
        feature.setAvgNestingDepth(nestingDepths.stream().mapToInt(p -> p.getSecond()).average().orElse(0.0));
        feature.setMaxNestingDepth(nestingDepths.stream().mapToInt(p -> p.getSecond()).max().orElse(0));
        feature.setMinNestingDepth(nestingDepths.stream().mapToInt(p -> p.getSecond()).min().orElse(0));
    }

    @Override
    public void onSuccess() {
        callback.onComplete(feature);
    }
}
