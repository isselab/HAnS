// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.isselab.HAnS.fileAnnotation.psi.*;

public class FileAnnotationFeatureNameImpl extends ASTWrapperPsiElement implements FileAnnotationFeatureName {

  public FileAnnotationFeatureNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FileAnnotationVisitor visitor) {
    visitor.visitFeatureName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FileAnnotationVisitor) accept((FileAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

}
