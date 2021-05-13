// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.featureModel.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.referencing.FeatureAnnotationNamedElement;

public interface FeatureModelFeature extends FeatureAnnotationNamedElement {

  @NotNull
  List<FeatureModelFeature> getFeatureList();

  String getLPQ();

  String renameFeature();

  String addFeature();

  int deleteFeature();

  String getName();

  PsiElement setName(String newName);

  PsiElement getNameIdentifier();

  String getFeatureName();

}
