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
package se.isselab.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationFeature;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;
import se.isselab.HAnS.unassignedFeature.UnassignedFeatureQuickFix;

import java.util.List;

public class CodeAnnotationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof CodeAnnotationLpq)){
            return;
        }

        List<CodeAnnotationFeature> featureNames = ((CodeAnnotationLpq) element).getFeatureList();
        for (CodeAnnotationFeature feature : featureNames) {

            if (FeatureModelUtil.findLPQ(element.getProject(), element.getText()).isEmpty()) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved property: Feature is not defined in the Feature Model")
                        .range(feature.getTextRange())
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        .withFix(new UnassignedFeatureQuickFix(element.getText())) // &line[Quickfix]
                        .create();
            } else {
                // Found at least one property, force the text attributes to Simple syntax value character
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(feature.getTextRange()).textAttributes(FeatureModelSyntaxHighlighter.FEATURE).create();
            }
        }
    }
}
