// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.isselab.HAnS.fileAnnotation.psi.*;

public class FileAnnotationLpqReferencesImpl extends ASTWrapperPsiElement implements FileAnnotationLpqReferences {

  public FileAnnotationLpqReferencesImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FileAnnotationVisitor visitor) {
    visitor.visitLpqReferences(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FileAnnotationVisitor) accept((FileAnnotationVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FileAnnotationLpq> getLpqList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FileAnnotationLpq.class);
  }

}
