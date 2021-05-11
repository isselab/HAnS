// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.folderAnnotation.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class FolderAnnotationVisitor extends PsiElementVisitor {

  public void visitFeature(@NotNull FolderAnnotationFeature o) {
    visitPsiElement(o);
  }

  public void visitLpq(@NotNull FolderAnnotationLpq o) {
    visitNamedElement(o);
  }

  public void visitNamedElement(@NotNull FolderAnnotationNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
