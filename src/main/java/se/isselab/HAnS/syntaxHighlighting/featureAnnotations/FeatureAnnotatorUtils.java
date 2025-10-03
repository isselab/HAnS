package se.isselab.HAnS.syntaxHighlighting.featureAnnotations;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.syntaxHighlighting.featureModel.FeatureModelSyntaxHighlighter;
import se.isselab.HAnS.unassignedFeature.UnassignedFeatureQuickFix;

public final class FeatureAnnotatorUtils {


    /**
     * Annotates a feature reference in the editor based on whether it is defined in the Feature Model.
     * <p>
     * This method checks if the given {@code element} (typically a reference to a feature) resolves to a known
     * {@link se.isselab.HAnS.featureModel.psi.FeatureModelFeature} in the project. If no matching feature is found via either LPQ or full LPQ lookup,
     * it creates an error annotation indicating the feature is unresolved and suggests a quick fix.
     * If the feature is found, it applies a silent annotation to style the reference appropriately.
     * </p>
     *
     * @param element the PSI element representing the feature reference in code.
     * @param feature the PSI element whose text range should be annotated.
     * @param holder the {@link AnnotationHolder} used to register annotations in the editor.
     */
    public static void annotateFeatureReference(PsiElement element, PsiElement feature, AnnotationHolder holder) {
        if (FeatureModelUtil.findLPQ(element.getProject(), element.getText()).isEmpty()
                && (FeatureModelUtil.findFullLPQ(element.getProject(), element.getText()).isEmpty())) {
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
