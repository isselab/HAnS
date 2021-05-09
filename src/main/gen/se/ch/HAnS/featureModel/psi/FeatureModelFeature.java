// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.featureModel.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface FeatureModelFeature extends PsiElement {

  @NotNull
  List<FeatureModelFeature> getFeatureList();

  String renameFeature();

  String addFeature();

  int deleteFeature();

}
