package se.ch.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationFeature;
import se.ch.HAnS.featureModel.FeatureModelUtil;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;

import java.util.List;

public class CodeAnnotationAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof CodeAnnotationFeature)){
            return;
        }

        CodeAnnotationFeature feature = (CodeAnnotationFeature) element;
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
}
