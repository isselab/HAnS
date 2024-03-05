// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.referencing.impl.FeatureAnnotationNamedElementImpl;
import se.isselab.HAnS.featureModel.psi.*;
import com.intellij.navigation.ItemPresentation;
import java.util.Deque;

public class FeatureModelFeatureImpl extends FeatureAnnotationNamedElementImpl implements FeatureModelFeature {

  public FeatureModelFeatureImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureModelVisitor visitor) {
    visitor.visitFeature(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureModelVisitor) accept((FeatureModelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FeatureModelFeature> getFeatureList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FeatureModelFeature.class);
  }

  @Override
  public String getLPQText() {
    return FeatureModelPsiImplUtil.getLPQText(this);
  }

  @Override
  public Deque<PsiElement> getLPQStack() {
    return FeatureModelPsiImplUtil.getLPQStack(this);
  }

  @Override
  public void renameFeature() {
    FeatureModelPsiImplUtil.renameFeature(this);
  }

  @Override
  public void addFeature() {
    FeatureModelPsiImplUtil.addFeature(this);
  }

  @Override
  public int deleteFeature() {
    return FeatureModelPsiImplUtil.deleteFeature(this);
  }

  @Override
  public String getName() {
    return FeatureModelPsiImplUtil.getName(this);
  }

  @Override
  public FeatureModelFeature setName(String newName) {
    return FeatureModelPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return FeatureModelPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public String getFeatureName() {
    return FeatureModelPsiImplUtil.getFeatureName(this);
  }

  @Override
  public ItemPresentation getPresentation() {
    return FeatureModelPsiImplUtil.getPresentation(this);
  }

}
