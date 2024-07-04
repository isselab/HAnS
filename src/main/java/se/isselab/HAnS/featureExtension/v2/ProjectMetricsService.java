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

package se.isselab.HAnS.featureExtension.v2;

import com.intellij.openapi.components.Service;

import com.intellij.openapi.project.Project;

import se.isselab.HAnS.featureExtension.v2.backgroundTasks.ProjectMetricsBackgroundTask;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;

@Service(Service.Level.PROJECT)
public final class ProjectMetricsService implements MetricsService {

    private final Project project;

    public ProjectMetricsService(Project project){
        this.project = project;
    }

    @Override
    public void getProjectMetricsBackground(MetricsCallback callback) {
        ProjectMetricsBackgroundTask task = new ProjectMetricsBackgroundTask(project, "Refreshing metrics...", callback, null);
        task.queue();
    }

    @Override
    public boolean isRootFeature(FeatureModelFeature feature) {
        return feature.getParent() instanceof FeatureModelFile;
    }
}
