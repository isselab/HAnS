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

package se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;

import java.util.Map;

public class GetFeatureFileMappings extends Task.Backgroundable{

    private Map<String, FeatureFileMapping> featureFileMappings;
    private final FeatureFileMappingCallback callback;

    public GetFeatureFileMappings(@Nullable Project project, @NotNull String title, FeatureFileMappingCallback callback) {
        super(project, title);
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
    }

    @Override
    public void onSuccess() {
        callback.onComplete(featureFileMappings);
    }
}
