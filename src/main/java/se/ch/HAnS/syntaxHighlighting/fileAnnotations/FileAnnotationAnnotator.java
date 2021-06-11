/*
Copyright [2021] [Herman Jansson & Johan Martinson]

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
package se.ch.HAnS.syntaxHighlighting.fileAnnotations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.FeatureModelUtil;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFeatureName;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.ch.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;

import java.util.List;

public class FileAnnotationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof FileAnnotationLpq)){
            if(checkElementOftypeFileAnnotationFileName(element)){
                annotateFileName(element, holder);
            }
            return;

        }

        List<FileAnnotationFeatureName> featureNames = ((FileAnnotationLpq) element).getFeatureNameList();
        for (FileAnnotationFeatureName feature : featureNames) {

            if (FeatureModelUtil.findLPQ(element.getProject(), element.getText()).isEmpty()) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved property: Feature is not defined in the Feature Model")
                        .range(feature.getTextRange())
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
                        //.withFix(new FeatureModelCreateNewFeature(featureText))
                        .create();
            } else {
                // Found at least one property, force the text attributes to Simple syntax value character
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(feature.getTextRange()).textAttributes(FeatureModelSyntaxHighlighter.FEATURE).create();
            }
        }
    }

    private void annotateFileName(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        FileAnnotationFileName fileName = (FileAnnotationFileName) element;
        String fileNameText = fileName.getText();
        TextRange fileNameRange = fileName.getTextRange();

        PsiFile[] files = element.getContainingFile().getContainingDirectory().getFiles();
        boolean nameExistsInDirectory = false;

        for (PsiFile file: files) {
            if (file.getName().equals(fileNameText)) nameExistsInDirectory = true;
        }
        if (!nameExistsInDirectory) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved Property: File don't exist in this directory")
                    .range(fileNameRange)
                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                    // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
                    //.withFix(new FeatureModelCreateNewFeature(featureText))
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(fileNameRange)
                    .textAttributes(FileAnnotationSyntaxHighlighter.FILENAME)
                    .create();
        }
    }

    private boolean checkElementOftypeFileAnnotationFileName(@NotNull PsiElement element) {
        return element instanceof FileAnnotationFileName;
    }
}
