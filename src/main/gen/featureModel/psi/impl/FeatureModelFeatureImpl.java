// This is a generated file. Not intended for manual editing.
package featureModel.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static featureModel.psi.FeatureModelTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import featureModel.psi.*;

public class FeatureModelFeatureImpl extends ASTWrapperPsiElement implements FeatureModelFeature {

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
  @Nullable
  public FeatureModelFeature getFeature() {
    return findChildByClass(FeatureModelFeature.class);
  }

}
