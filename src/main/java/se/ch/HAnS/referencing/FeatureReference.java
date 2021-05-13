package se.ch.HAnS.referencing;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.ch.HAnS.AnnotationIcons;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.ch.HAnS.codeAnnotation.psi.impl.CodeAnnotationPsiImplUtil;
import se.ch.HAnS.featureModel.FeatureModelUtil;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelPsiImplUtil;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.ch.HAnS.fileAnnotation.psi.impl.FileAnnotationPsiImplUtil;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;
import se.ch.HAnS.folderAnnotation.psi.impl.FolderAnnotationPsiImplUtil;

import java.util.*;

public class FeatureReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String lpq;
    private String newLpq;
    Deque<Integer> stack = new ArrayDeque<Integer>();

    public FeatureReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        lpq = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    private String findNewLPQ(PsiElement element, String string) {
        if (FeatureModelUtil.isAvailableLPQ(myElement.getProject(), string)) {
            return string;
        }
        PsiElement parentElement = element.getParent();
        String parentString = element.getParent().getFirstChild().getText();
        String s = parentString.concat("::" + string);
        return findNewLPQ(parentElement, s);
    }

    // TODO: Fix so that the method renames to an LPQ but without check for each reference.
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        FeatureModelFeature declarationElement = (FeatureModelFeature) myElement.getReferences()[0].resolve();
        if (declarationElement != null) {
            newLpq = findNewLPQ(declarationElement, newElementName);
        }

        if (myElement instanceof FolderAnnotationLpq) {
            FolderAnnotationPsiImplUtil.setName((FolderAnnotationLpq) myElement, newLpq);
        }
        else if (myElement instanceof FileAnnotationLpq) {
            FileAnnotationPsiImplUtil.setName((FileAnnotationLpq) myElement, newLpq);
        }
        else if (myElement instanceof CodeAnnotationLpq) {
            CodeAnnotationPsiImplUtil.setName((CodeAnnotationLpq) myElement, newLpq);
        }
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