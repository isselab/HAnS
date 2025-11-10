/*
Copyright 2021 Herman Jansson & Johan Martinson

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
package se.isselab.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Collection;


/**
 * Provides feature names for code completion in feature-to-file and feature-to-folder files.
 */
public class FeatureNameCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        String prefix = result.getPrefixMatcher().getPrefix();
        if (prefix.isEmpty()) {
            return;
        }

        Project project = parameters.getEditor().getProject();
        if (project == null) {
            return;
        }

        // Get all features from the project's feature models
        Collection<FeatureModelFeature> features = FeatureModelUtil.findFeatures(project);

        for (FeatureModelFeature feature : features) {
            String featureName = feature.getFeatureName();
            if (featureName != null && featureName.startsWith(prefix)) {
                result.addElement(
                    LookupElementBuilder.create(featureName)
                        .withItemTextForeground(JBColor.BLUE)
                        .withTypeText("Feature", true)
                );
            }
        }
    }
}

