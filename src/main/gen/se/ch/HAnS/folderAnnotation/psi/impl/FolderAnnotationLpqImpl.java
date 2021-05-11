// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.folderAnnotation.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static se.ch.HAnS.folderAnnotation.psi.FolderAnnotationTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.ch.HAnS.folderAnnotation.psi.*;

public class FolderAnnotationLpqImpl extends ASTWrapperPsiElement implements FolderAnnotationLpq {

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

}
