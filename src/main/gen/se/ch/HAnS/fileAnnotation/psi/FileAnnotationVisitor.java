// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.fileAnnotation.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;

public class FileAnnotationVisitor extends PsiElementVisitor {

  public void visitFeatureName(@NotNull FileAnnotationFeatureName o) {
    visitPsiElement(o);
  }

  public void visitFileAnnotation(@NotNull FileAnnotationFileAnnotation o) {
    visitPsiElement(o);
  }

  public void visitFileName(@NotNull FileAnnotationFileName o) {
    visitPsiElement(o);
  }

  public void visitFileReference(@NotNull FileAnnotationFileReference o) {
    visitPsiElement(o);
  }

  public void visitFileReferences(@NotNull FileAnnotationFileReferences o) {
    visitPsiElement(o);
  }

  public void visitLpq(@NotNull FileAnnotationLpq o) {
    visitNamedElement(o);
  }

  public void visitLpqReferences(@NotNull FileAnnotationLpqReferences o) {
    visitPsiElement(o);
  }

  public void visitNamedElement(@NotNull FileAnnotationNamedElement o) {
    visitPsiElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
