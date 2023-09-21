// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.folderAnnotation.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.referencing.impl.FeatureAnnotationNamedElementImpl;
import se.isselab.HAnS.folderAnnotation.psi.*;

public class FolderAnnotationLpqImpl extends FeatureAnnotationNamedElementImpl implements FolderAnnotationLpq {

  public FolderAnnotationLpqImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FolderAnnotationVisitor visitor) {
    visitor.visitLpq(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FolderAnnotationVisitor) accept((FolderAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FolderAnnotationFeature> getFeatureList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FolderAnnotationFeature.class);
  }

  @Override
  public String getName() {
    return FolderAnnotationPsiImplUtil.getName(this);
  }

  @Override
  public FolderAnnotationLpq setName(String newName) {
    return FolderAnnotationPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return FolderAnnotationPsiImplUtil.getNameIdentifier(this);
  }

}
