// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.codeAnnotation.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.referencing.impl.FeatureAnnotationNamedElementImpl;
import se.isselab.HAnS.codeAnnotation.psi.*;

public class CodeAnnotationLpqImpl extends FeatureAnnotationNamedElementImpl implements CodeAnnotationLpq {

  public CodeAnnotationLpqImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull CodeAnnotationVisitor visitor) {
    visitor.visitLpq(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof CodeAnnotationVisitor) accept((CodeAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<CodeAnnotationFeature> getFeatureList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, CodeAnnotationFeature.class);
  }

  @Override
  public String getName() {
    return CodeAnnotationPsiImplUtil.getName(this);
  }

  @Override
  public CodeAnnotationLpq setName(String newName) {
    return CodeAnnotationPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return CodeAnnotationPsiImplUtil.getNameIdentifier(this);
  }

}
