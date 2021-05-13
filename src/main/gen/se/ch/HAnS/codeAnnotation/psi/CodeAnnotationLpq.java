// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.codeAnnotation.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.referencing.FeatureAnnotationNamedElement;

public interface CodeAnnotationLpq extends FeatureAnnotationNamedElement {

  @NotNull
  List<CodeAnnotationFeature> getFeatureList();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

}
