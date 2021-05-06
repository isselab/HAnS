package se.ch.HAnS.syntaxHighlighting.fileAnnotations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.FeatureModelUtil;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFeatureName;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationFeature;
import se.ch.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;

import java.util.Arrays;
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
            holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved property")
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

        List<PsiFile> files = Arrays.asList(element.getContainingFile().getContainingDirectory().getFiles());
        for (PsiFile file: files) {
            System.out.println(file.getName() + " == " + fileNameText);
            if (!(file.getName().equals(fileNameText))) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unresolved Property")
                        .range(fileNameRange)
                        .highlightType(ProblemHighlightType.LIKE_UNKNOWN_SYMBOL)
                        // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
                        //.withFix(new FeatureModelCreateNewFeature(featureText))
                        .create();
            } else {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(fileNameRange)
                        .textAttributes(DefaultLanguageHighlighterColors.KEYWORD)
                        .create();
            }
        }


    }

    private boolean checkElementOftypeFileAnnotationFileName(@NotNull PsiElement element) {
        return element instanceof FileAnnotationFileName;
    }
}
