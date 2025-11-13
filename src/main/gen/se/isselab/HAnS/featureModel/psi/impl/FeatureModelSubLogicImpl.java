// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static se.isselab.HAnS.featureModel.psi.FeatureModelTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.isselab.HAnS.featureModel.psi.*;

public class FeatureModelSubLogicImpl extends ASTWrapperPsiElement implements FeatureModelSubLogic {

  public FeatureModelSubLogicImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureModelVisitor visitor) {
    visitor.visitSubLogic(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureModelVisitor) accept((FeatureModelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FeatureModelComponent> getComponentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FeatureModelComponent.class);
  }

}
