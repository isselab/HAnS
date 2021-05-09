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
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFeatureName;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.ch.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;

import java.util.List;

public class FileAnnotationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof FileAnnotationFeatureName)){
            if(checkElementOftypeFileAnnotationFileName(element)){
                annotateFileName(element, holder);
            }
            return;

        }

        FileAnnotationFeatureName feature = (FileAnnotationFeatureName) element;
        String featureText = feature.getText();

        TextRange featureRange = feature.getTextRange();

        List<FeatureModelFeature> features = FeatureModelUtil.findFeatures(element.getProject(), featureText);
        if (features.isEmpty()) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved property: Feature is not defined in the Feature Model")
                    .range(featureRange)
                    .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                    // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
                    //.withFix(new FeatureModelCreateNewFeature(featureText))
                    .create();
        } else {
            // Found at least one property, force the text attributes to Simple syntax value character
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(featureRange).textAttributes(FeatureModelSyntaxHighlighter.FEATURE).create();
        }


    }

    private void annotateFileName(PsiElement element, AnnotationHolder holder) {
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
