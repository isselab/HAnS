// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.folderAnnotation.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.referencing.FeatureAnnotationNamedElement;

public class FolderAnnotationVisitor extends PsiElementVisitor {

  public void visitFeature(@NotNull FolderAnnotationFeature o) {
    visitPsiElement(o);
  }

  public void visitLpq(@NotNull FolderAnnotationLpq o) {
    visitFeatureAnnotationNamedElement(o);
  }

  public void visitFeatureAnnotationNamedElement(@NotNull FeatureAnnotationNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
