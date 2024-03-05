// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.referencing.impl.FeatureAnnotationNamedElementImpl;
import se.isselab.HAnS.fileAnnotation.psi.*;

public class FileAnnotationLpqImpl extends FeatureAnnotationNamedElementImpl implements FileAnnotationLpq {

  public FileAnnotationLpqImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FileAnnotationVisitor visitor) {
    visitor.visitLpq(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FileAnnotationVisitor) accept((FileAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FileAnnotationFeatureName> getFeatureNameList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FileAnnotationFeatureName.class);
  }

  @Override
  public String getName() {
    return FileAnnotationPsiImplUtil.getName(this);
  }

  @Override
  public FileAnnotationLpq setName(String newName) {
    return FileAnnotationPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return FileAnnotationPsiImplUtil.getNameIdentifier(this);
  }

}
