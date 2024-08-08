/*
Copyright 2024 Johan Martinson, David Stechow & Philipp Kusmierz

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
package se.isselab.HAnS.pluginExtensions;

import com.intellij.openapi.project.Project;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.fileHighlighter.FileHighlighter;

public interface HighlighterService {

    /**
     * Highlights a feature in the feature model
     * @param featureLpq LPQ name of the feature
     * @see FileHighlighter#highlightFeatureInFeatureModel(Project, String)
     */
    void highlightFeatureInFeatureModel(String featureLpq);

    /**
     * Highlights a feature in the feature model
     * @param feature {@link FeatureModelFeature}
     * @see FileHighlighter#highlightFeatureInFeatureModel(FeatureModelFeature)
     */
    void highlighFeatureInFeatureModel(FeatureModelFeature feature);

    /**
     * Opens a file of the project in the editor
     * @param path String: absolute path of the file
     */
    void openFileInProject(String path);

    /**
     * Opens a file of the project in the editor and highlights code block
     * @param path String: Absolute path of the file
     * @param startline of the codeblock
     * @param endline of the codeblock
     */
    void openFileInProject(String path, int startline, int endline);
}
