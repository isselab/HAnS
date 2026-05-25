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

public class FeatureModelComponentImpl extends ASTWrapperPsiElement implements FeatureModelComponent {

  public FeatureModelComponentImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureModelVisitor visitor) {
    visitor.visitComponent(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureModelVisitor) accept((FeatureModelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public FeatureModelLogic getLogic() {
    return findChildByClass(FeatureModelLogic.class);
  }

  @Override
  @Nullable
  public FeatureModelFeature getFeature() {
    return findChildByClass(FeatureModelFeature.class);
  }

}
