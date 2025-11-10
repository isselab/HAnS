// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.referencing.FeatureAnnotationNamedElement;
import com.intellij.navigation.ItemPresentation;
import java.util.Deque;

public interface FeatureModelFeature extends FeatureAnnotationNamedElement {

  @NotNull
  List<FeatureModelFeature> getFeatureList();

  String getFullLPQText();

  @Nullable
  FeatureModelGroupModifier getGroupModifier();

  @Nullable
  FeatureModelOptionalModifier getOptionalModifier();

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

  String addToFeatureModel(String newName);

  FeatureModelFeature deleteFromFeatureModel();

  void moveFeatureWithChildren(@NotNull FeatureModelFeature childFeature);

  FeatureModelFeature deleteFeatureWithAnnotations();

  boolean deleteFeatureWithCode();

  int getTanglingDegree();

  void setTanglingDegree(int tanglingDegree);

  int getScatteringDegree();

  void setScatteringDegree(int scatteringDegree);

  int getLineCount();

  void setLineCount(int lineCount);

  int getMaxNestingDepth();

  void setMaxNestingDepth(int maxNestingDepth);

  int getMinNestingDepth();

  void setMinNestingDepth(int minNestingDepth);

  double getAvgNestingDepth();

  void setAvgNestingDepth(double avgNestingDepth);

  int getNumberOfAnnotatedFiles();

  void setNumberOfAnnotatedFiles(int numberOfAnnotatedFiles);

  int getNumberOfFileAnnotations();

  void setNumberOfFileAnnotations(int numberOfFileAnnotations);

  int getNumberOfFolderAnnotations();

  void setNumberOfFolderAnnotations(int numberOfFolderAnnotations);

}
