// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.isselab.HAnS.fileAnnotation.psi.*;

public class FileAnnotationFileAnnotationImpl extends ASTWrapperPsiElement implements FileAnnotationFileAnnotation {

  public FileAnnotationFileAnnotationImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FileAnnotationVisitor visitor) {
    visitor.visitFileAnnotation(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FileAnnotationVisitor) accept((FileAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public FileAnnotationFileReferences getFileReferences() {
    return findNotNullChildByClass(FileAnnotationFileReferences.class);
  }

  @Override
  @NotNull
  public FileAnnotationLpqReferences getLpqReferences() {
    return findNotNullChildByClass(FileAnnotationLpqReferences.class);
  }

}
