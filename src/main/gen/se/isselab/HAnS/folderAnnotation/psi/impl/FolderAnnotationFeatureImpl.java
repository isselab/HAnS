// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.folderAnnotation.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.isselab.HAnS.folderAnnotation.psi.*;

public class FolderAnnotationFeatureImpl extends ASTWrapperPsiElement implements FolderAnnotationFeature {

  public FolderAnnotationFeatureImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FolderAnnotationVisitor visitor) {
    visitor.visitFeature(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FolderAnnotationVisitor) accept((FolderAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

}
