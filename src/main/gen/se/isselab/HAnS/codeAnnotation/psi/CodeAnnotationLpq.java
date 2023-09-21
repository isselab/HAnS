// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.codeAnnotation.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.referencing.FeatureAnnotationNamedElement;

public interface CodeAnnotationLpq extends FeatureAnnotationNamedElement {

  @NotNull
  List<CodeAnnotationFeature> getFeatureList();

  String getName();

  CodeAnnotationLpq setName(String newName);

  PsiElement getNameIdentifier();

}
