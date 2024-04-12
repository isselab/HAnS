// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.referencing.FeatureAnnotationNamedElement;

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
    visitFeatureAnnotationNamedElement(o);
  }

  public void visitLpqReferences(@NotNull FileAnnotationLpqReferences o) {
    visitPsiElement(o);
  }

  public void visitFeatureAnnotationNamedElement(@NotNull FeatureAnnotationNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
