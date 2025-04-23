/*
Copyright 2025 Johan Martinson

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
package se.isselab.HAnS.trafficLight;

import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import java.util.Map;
import java.util.Set;

public class HansTrafficLightDashboardModel {
    private final boolean isAlive;
    private final int featureCount;
    private final @Nullable Map<String, Set<String>> filePathsFeatureFileMapping;
    private final @Nullable Map<String, Set<String>> filePathsFeatureFolderMapping;

    public HansTrafficLightDashboardModel(boolean isAlive, Map<String, Map<String, Set<String>>> result) {
        this.isAlive = isAlive;
        this.featureCount = result.values().stream()
                .flatMap(innerMap -> innerMap.values().stream())
                .mapToInt(Set::size)
                .sum();
        this.filePathsFeatureFileMapping = result.get(FeatureFileMapping.AnnotationType.FILE.toString());
        this.filePathsFeatureFolderMapping = result.get(FeatureFileMapping.AnnotationType.FOLDER.toString());
    }

    public HansTrafficLightDashboardModel(boolean isAlive) {
        this.isAlive = isAlive;
        this.featureCount = 0;
        this.filePathsFeatureFileMapping = null;
        this.filePathsFeatureFolderMapping = null;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int findingsCount() {
        return featureCount;
    }

    public boolean hasFindings() {
        return findingsCount() != 0;
    }

    public Map<String, Set<String>> getFilePathsFeatureFileMapping() {
        return filePathsFeatureFileMapping;
    }

    public Map<String, Set<String>> getFilePathsFeatureFolderMapping() {
        return filePathsFeatureFolderMapping;
    }
}


