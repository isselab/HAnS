// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.fileAnnotation.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.referencing.FeatureAnnotationNamedElement;

public interface FileAnnotationLpq extends FeatureAnnotationNamedElement {

  @NotNull
  List<FileAnnotationFeatureName> getFeatureNameList();

  String getName();

  FileAnnotationLpq setName(String newName);

  PsiElement getNameIdentifier();

}
