// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.fileAnnotations.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import se.ch.HAnS.fileAnnotations.psi.*;

public class FileAnnotationsFeatureNameImpl extends ASTWrapperPsiElement implements FileAnnotationsFeatureName {

  public FileAnnotationsFeatureNameImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FileAnnotationsVisitor visitor) {
    visitor.visitFeatureName(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FileAnnotationsVisitor) accept((FileAnnotationsVisitor)visitor);
    else super.accept(visitor);
  }

}
