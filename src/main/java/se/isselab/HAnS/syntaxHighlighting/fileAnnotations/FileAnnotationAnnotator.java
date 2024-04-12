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
package se.isselab.HAnS.syntaxHighlighting.fileAnnotations;

import com.intellij.codeInsight.daemon.quickFix.CreateFilePathFix;
import com.intellij.codeInsight.daemon.quickFix.NewFileLocation;
import com.intellij.codeInsight.daemon.quickFix.TargetDirectory;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFeatureName;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.isselab.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;
import se.isselab.HAnS.unassignedFeature.UnassignedFeatureQuickFix;
import java.util.Arrays;
import java.util.List;

public class FileAnnotationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof FileAnnotationLpq)){
            if(checkElementOfTypeFileAnnotationFileName(element)){
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
                        .withFix(new UnassignedFeatureQuickFix(element.getText())) // &line[Quickfix]
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

        var directory = element.getContainingFile().getContainingDirectory();
        PsiFile[] files = directory.getFiles();
        boolean nameExistsInDirectory = false;

        for (PsiFile file: files) {
            if (file.getName().equals(fileNameText)) nameExistsInDirectory = true;
        }
        if (!nameExistsInDirectory) {
            var targetDirectories = Arrays.stream(new TargetDirectory[]{new TargetDirectory(directory)}).toList();
            holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved Property: File don't exist in this directory")
                    .range(fileNameRange)
                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                    .withFix(new CreateFilePathFix(element, new NewFileLocation(targetDirectories, fileNameText))) // &line[Quickfix]
                    .create();
        } else {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(fileNameRange)
                    .textAttributes(FileAnnotationSyntaxHighlighter.FILENAME)
                    .create();
        }
    }

    private boolean checkElementOfTypeFileAnnotationFileName(@NotNull PsiElement element) {
        return element instanceof FileAnnotationFileName;
    }
}
