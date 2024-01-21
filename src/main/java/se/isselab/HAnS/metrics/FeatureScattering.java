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

package se.isselab.HAnS.metrics;

import com.intellij.openapi.project.Project;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.SortedSet;
import java.util.TreeSet;


public class FeatureScattering {

    /**
     * Returns the scattering degree of the given feature while making use of a pre-calculated fileMapping
     *
     * @param featureFileMapping fileMapping of the feature to search for
     * @return scattering degree of the given feature
     */
    public static int getScatteringDegree(FeatureFileMapping featureFileMapping) {
        int scatteringDegree = 0;

        for (var file : featureFileMapping.getMappedFilePaths()) {
            var locations = featureFileMapping.getFeatureLocationsForFile(file);
            //use sorted set to provide O(logN) complexity  -  used for sorting and traversing
            SortedSet<Integer> lines = new TreeSet<>();
            //get all blocks of code annotated with the feature for the given file
            var blocks = locations.getFeatureLocations();

            if (blocks.isEmpty())
                continue;


            //get line numbers annotated by the blocks
            for (var block : blocks) {
                for (int i = block.getStartLine(); i <= block.getEndLine(); i++) {
                    lines.add(i);
                }
            }

            //if there were no lines then continue
            if (lines.isEmpty())
                continue;

            //count segments annotated by the feature within a file and increase scattering degree for each
            int prevLine = lines.first();
            for (int line : lines) {
                if (prevLine + 1 != line) {
                    scatteringDegree++;
                }
                prevLine = line;
            }

        }

        return scatteringDegree;
    }

    /**
     * Returns the scattering degree of the given feature
     *
     * @param feature feature to search for
     * @return scattering degree of the given feature
     */
    public static int getScatteringDegree(Project project, FeatureModelFeature feature) {
        return getScatteringDegree(FeatureLocationManager.getFeatureFileMapping(project, feature));
    }
}
