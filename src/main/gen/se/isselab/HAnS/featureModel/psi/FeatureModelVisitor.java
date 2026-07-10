// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.referencing.FeatureAnnotationNamedElement;

public class FeatureModelVisitor extends PsiElementVisitor {

  public void visitBooleanExpression(@NotNull FeatureModelBooleanExpression o) {
    visitPsiElement(o);
  }

  public void visitConstraint(@NotNull FeatureModelConstraint o) {
    visitPsiElement(o);
  }

  public void visitFeature(@NotNull FeatureModelFeature o) {
    visitFeatureAnnotationNamedElement(o);
  }

  public void visitFeatureOrGroup(@NotNull FeatureModelFeatureOrGroup o) {
    visitPsiElement(o);
  }

  public void visitOptionality(@NotNull FeatureModelOptionality o) {
    visitPsiElement(o);
  }

  public void visitOrGroup(@NotNull FeatureModelOrGroup o) {
    visitPsiElement(o);
  }

  public void visitXorGroup(@NotNull FeatureModelXorGroup o) {
    visitPsiElement(o);
  }

  public void visitFeatureAnnotationNamedElement(@NotNull FeatureAnnotationNamedElement o) {
    visitElement(o);
  }

  public void visitPsiElement(@NotNull PsiElement o) {
    visitElement(o);
  }

}
