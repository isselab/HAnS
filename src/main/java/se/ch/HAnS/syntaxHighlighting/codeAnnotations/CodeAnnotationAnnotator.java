package se.ch.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationFeature;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.ch.HAnS.featureModel.FeatureModelUtil;
import se.ch.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;

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
}
