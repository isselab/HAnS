package se.ch.HAnS.featureModel.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelNamedElement;

public abstract class FeatureModelNamedElementImpl extends ASTWrapperPsiElement implements FeatureModelNamedElement {

    public FeatureModelNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    /*
    @NotNull
    @Override
    public PsiReference @NotNull [] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }*/

}