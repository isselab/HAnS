// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.featureModel.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.referencing.FeatureAnnotationNamedElement;
import com.intellij.navigation.ItemPresentation;
import java.util.Deque;

public interface FeatureModelFeature extends FeatureAnnotationNamedElement {

  @NotNull
  List<FeatureModelFeature> getFeatureList();

  String getLPQText();

  Deque<PsiElement> getLPQStack();

  void renameFeature();

  void addFeature();

  int deleteFeature();

  String getName();

  FeatureModelFeature setName(String newName);

  PsiElement getNameIdentifier();

  String getFeatureName();

  ItemPresentation getPresentation();

}
