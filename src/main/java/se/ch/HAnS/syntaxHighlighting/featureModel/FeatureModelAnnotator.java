package se.ch.HAnS.syntaxHighlighting.featureModel;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;

public class FeatureModelAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof FeatureModelFeature)){
            return;
        }
        FeatureModelFeature feature = (FeatureModelFeature) element;
        // Removes project node from annotation check
        if (dontCheckProjectNode(feature)){
            return;
        }

        Query<PsiReference> psiReferences = ReferencesSearch.search(feature);
        System.out.println(psiReferences.findFirst() + " and element is: " + feature.getFeatureName());
        if (psiReferences.findFirst() == null) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Feature is never used")
                    .range(feature.getFirstChild().getTextRange())
                    .highlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL)
                    // ** Tutorial step 18.3 - Add a quick fix for the string containing possible properties
                    //.withFix(new FeatureModelCreateNewFeature(featureText))
                    .create();
        } else {
            // Found at least one property, force the text attributes to Simple syntax value character
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(feature.getTextRange()).textAttributes(FeatureModelSyntaxHighlighter.FEATURE).create();
        }
    }

    private boolean dontCheckProjectNode(FeatureModelFeature feature){
        return feature.getContainingFile().getFirstChild() == feature;
    }
}
