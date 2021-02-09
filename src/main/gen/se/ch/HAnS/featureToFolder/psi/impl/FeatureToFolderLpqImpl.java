// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.featureToFolder.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static se.ch.HAnS.featureToFolder.psi.FeatureToFolderTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.ch.HAnS.featureToFolder.psi.*;

public class FeatureToFolderLpqImpl extends ASTWrapperPsiElement implements FeatureToFolderLpq {

  public FeatureToFolderLpqImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureToFolderVisitor visitor) {
    visitor.visitLpq(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureToFolderVisitor) accept((FeatureToFolderVisitor)visitor);
    else super.accept(visitor);
  }

}
