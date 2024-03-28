// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi.impl;

import java.util.ArrayList;
import java.util.List;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.referencing.impl.FeatureAnnotationNamedElementImpl;
import se.isselab.HAnS.featureModel.psi.*;
import com.intellij.navigation.ItemPresentation;
import java.util.Deque;
import java.util.Objects;
import java.util.regex.Pattern;

public class FeatureModelFeatureImpl extends FeatureAnnotationNamedElementImpl implements FeatureModelFeature {

  public FeatureModelFeatureImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull FeatureModelVisitor visitor) {
    visitor.visitFeature(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof FeatureModelVisitor) accept((FeatureModelVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<FeatureModelFeature> getFeatureList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, FeatureModelFeature.class);
  }

  @Override
  public String getLPQText() {
    return FeatureModelPsiImplUtil.getLPQText(this);
  }

  @Override
  public Deque<PsiElement> getLPQStack() {
    return FeatureModelPsiImplUtil.getLPQStack(this);
  }

  @Override
  public void renameFeature() {
    FeatureModelPsiImplUtil.renameFeature(this);
  }

  @Override
  public void addFeature() {
    FeatureModelPsiImplUtil.addFeature(this);
  }

  @Override
  public boolean addToFeatureModel(String lpq) {
    if (lpq == null || "".equals(lpq.trim()) || !Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]*", lpq.trim())) {
      return false;
    }
    else {
      PsiElement[] l = ReadAction.compute(this::getChildren);
      for (PsiElement e : l) {
        if (Objects.requireNonNull(e.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText().equals(lpq)) {
          return false;
        }
      }
      Runnable r = () -> {
        FeatureModelPsiImplUtil.addFeatureToFeatureModel(this, lpq.trim());
      };
      WriteCommandAction.runWriteCommandAction(ReadAction.compute(this::getProject), r);
      return true;
    }
  }

  @Override
  public boolean renameInFeatureModel(String lpq) {
    if (lpq == null || "".equals(lpq.trim()) || !Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]*", lpq.trim())) {
      return false;
    }
    else {
      PsiElement parent = ReadAction.compute(this::getParent);
      if (parent != null && parent instanceof FeatureModelFeature) {
        PsiElement[] parentNodeChildren = ReadAction.compute(parent::getChildren);
        for (PsiElement e : parentNodeChildren) {
          // if child with same name already exists stop the execution
          if (Objects.requireNonNull(e.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText().equals(lpq)) {
            return false;
          }
        }
      }
      Runnable r = () -> {
        FeatureModelPsiImplUtil.setName(this, lpq.trim());
      };
      WriteCommandAction.runWriteCommandAction(ReadAction.compute(this::getProject), r);
      return true;
    }
  }

  @Override
  public void moveFeatureWithChildren(FeatureModelFeature childFeature) {
    Project projectInstance = ReadAction.compute(this::getProject);
    Runnable r = () -> {
      ReadAction.run(() -> {FeatureModelPsiImplUtil.moveFeatureWithChildren(this, childFeature);});
    };
    WriteCommandAction.runWriteCommandAction(projectInstance, r);
  }


  @Override
  public FeatureModelFeature deleteFromFeatureModel() {
    Runnable r = () -> {
      ReadAction.run(() -> {FeatureModelPsiImplUtil.deleteFromFeatureModel(this);});
    };
    WriteCommandAction.runWriteCommandAction(ReadAction.compute(this::getProject), r);
    return this;
  }

  @Override
  public FeatureModelFeature deleteFeatureWithAnnotations() {
    Runnable r = () -> {
      ReadAction.run(() -> {FeatureModelPsiImplUtil.deleteFeatureWithAnnotations(this);});
    };
    WriteCommandAction.runWriteCommandAction(ReadAction.compute(this::getProject), r);
    return this;
  }

  @Override
  public void addWithChildren(FeatureModelFeature childFeature) {
    Runnable r = () -> {
      ReadAction.run(() -> {FeatureModelPsiImplUtil.addFeatureWithChildren(this, childFeature);});
    };
    WriteCommandAction.runWriteCommandAction(ReadAction.compute(this::getProject), r);

  }

  @Override
  public int deleteFeature() {
    return FeatureModelPsiImplUtil.deleteFeature(this);
  }

  @Override
  public String getName() {
    return FeatureModelPsiImplUtil.getName(this);
  }

  @Override
  public FeatureModelFeature setName(String newName) {
    return FeatureModelPsiImplUtil.setName(this, newName);
  }

  @Override
  public PsiElement getNameIdentifier() {
    return FeatureModelPsiImplUtil.getNameIdentifier(this);
  }

  @Override
  public String getFeatureName() {
    return FeatureModelPsiImplUtil.getFeatureName(this);
  }

  @Override
  public ItemPresentation getPresentation() {
    return FeatureModelPsiImplUtil.getPresentation(this);
  }

}
