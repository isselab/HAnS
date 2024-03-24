// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi.impl;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.referencing.impl.FeatureAnnotationNamedElementImpl;
import se.isselab.HAnS.featureModel.psi.*;
import com.intellij.navigation.ItemPresentation;
import java.util.Deque;
import java.util.Objects;
import java.util.regex.Pattern;

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
  public String addToFeatureModel(String lpq) {
    if (lpq == null || "".equals(lpq.trim()) || !Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]*", lpq.trim())) {
      return null;
    }
    else {
      PsiElement[] l = this.getChildren();
      for (PsiElement e : l) {
        if (Objects.requireNonNull(e.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText().equals(lpq)) {
          return null;
        }
      }
      return FeatureModelPsiImplUtil.addToFeatureModel(this, lpq.trim());
    }
  }

  @Override
  public boolean renameInFeatureModel(String lpq) {
    if (lpq == null || "".equals(lpq.trim()) || !Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]*", lpq.trim())) {
      return false;
    }
    else {
      PsiElement parent = this.getParent();
      if (parent != null && parent instanceof FeatureModelFeature) {
        PsiElement[] parentNodeChildren = this.getParent().getChildren();
        for (PsiElement e : parentNodeChildren) {
          // if already exists child with same name stop the execution
          if (Objects.requireNonNull(e.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText().equals(lpq)) {
            return false;
          }
        }
      }
      Runnable r = () -> {
        FeatureModelPsiImplUtil.setName(this, lpq);
      };
      WriteCommandAction.runWriteCommandAction(ReadAction.compute(this::getProject), r);
      return true;
    }
//    FeatureModelPsiImplUtil.rename(this, newName);
  }

  @Override
  public FeatureModelFeature deleteFromFeatureModel() {
    return FeatureModelPsiImplUtil.deleteFromFeatureModel(this);
  }

  @Override
  public void addWithChildren(FeatureModelFeature childFeature) {
    FeatureModelPsiImplUtil.addFeatureWithChildren(this, childFeature);
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
