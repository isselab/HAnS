package se.ch.HAnS.featureModel;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.ch.HAnS.AnnotationIcons;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;
import se.ch.HAnS.folderAnnotation.psi.impl.FolderAnnotationPsiImplUtil;

import java.util.ArrayList;
import java.util.List;

public class FeatureModelReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String lpq;

    public FeatureModelReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        lpq = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        FolderAnnotationPsiImplUtil.setName((FolderAnnotationLpq) myElement, newElementName);
        //FileAnnotationPsiImplUtil.setName((FileAnnotationLpq) myElement, newElementName);
        //CodeAnnotationPsiImplUtil.setName((CodeAnnotationLpq) myElement, newElementName);
        return myElement;
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        final List<FeatureModelFeature> features = FeatureModelUtil.findLPQs(project, lpq);
        List<ResolveResult> results = new ArrayList<>();
        for (FeatureModelFeature feature : features) {
            results.add(new PsiElementResolveResult(feature));
        }
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        Project project = myElement.getProject();
        List<FeatureModelFeature> features = FeatureModelUtil.findFeatures(project);
        List<LookupElement> variants = new ArrayList<>();
        for (final FeatureModelFeature feature : features) {
            if (feature.getText() != null && feature.getText().length() > 0) {
                variants.add(LookupElementBuilder
                        .create(feature.getLPQ()).withIcon(AnnotationIcons.FileType)
                        .withTypeText(feature.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

}