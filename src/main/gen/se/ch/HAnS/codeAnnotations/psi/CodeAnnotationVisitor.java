// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.codeAnnotations.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class CodeAnnotationVisitor extends PsiElementVisitor {

  public void visitBeginmarker(@NotNull CodeAnnotationBeginmarker o) {
    visitPsiElement(o);
  }

  public void visitEndmarker(@NotNull CodeAnnotationEndmarker o) {
    visitPsiElement(o);
  }

  public void visitLinemarker(@NotNull CodeAnnotationLinemarker o) {
    visitPsiElement(o);
  }

  public void visitLpq(@NotNull CodeAnnotationLpq o) {
    visitPsiElement(o);
  }

  public void visitParameter(@NotNull CodeAnnotationParameter o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
