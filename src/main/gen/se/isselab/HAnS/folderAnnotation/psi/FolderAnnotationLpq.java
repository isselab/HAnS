// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.folderAnnotation.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.referencing.FeatureAnnotationNamedElement;

public interface FolderAnnotationLpq extends FeatureAnnotationNamedElement {

  @NotNull
  List<FolderAnnotationFeature> getFeatureList();

  String getName();

  FolderAnnotationLpq setName(String newName);

  PsiElement getNameIdentifier();

}
