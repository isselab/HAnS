// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.codeAnnotation.psi.impl;

import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.isselab.HAnS.codeAnnotation.psi.*;

public class CodeAnnotationLinemarkerImpl extends ASTWrapperPsiElement implements CodeAnnotationLinemarker {

  public CodeAnnotationLinemarkerImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CodeAnnotationVisitor visitor) {
    visitor.visitLinemarker(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CodeAnnotationVisitor) accept((CodeAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public CodeAnnotationParameter getParameter() {
    return findNotNullChildByClass(CodeAnnotationParameter.class);
  }

}
