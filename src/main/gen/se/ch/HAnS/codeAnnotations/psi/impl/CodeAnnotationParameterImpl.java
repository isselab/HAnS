// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.codeAnnotations.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static se.ch.HAnS.codeAnnotations.psi.CodeAnnotationTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.ch.HAnS.codeAnnotations.psi.*;

public class CodeAnnotationParameterImpl extends ASTWrapperPsiElement implements CodeAnnotationParameter {

  public CodeAnnotationParameterImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CodeAnnotationVisitor visitor) {
    visitor.visitParameter(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CodeAnnotationVisitor) accept((CodeAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CodeAnnotationLpq> getLpqList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CodeAnnotationLpq.class);
  }

}
