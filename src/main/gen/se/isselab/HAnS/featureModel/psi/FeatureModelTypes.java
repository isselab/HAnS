// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.featureModel.psi.impl.*;

public interface FeatureModelTypes {

  IElementType FEATURE = new FeatureModelElementType("FEATURE");

  IElementType CRLF = new FeatureModelTokenType("CRLF");
  IElementType DEDENT = new FeatureModelTokenType("DEDENT");
  IElementType FEATURENAME = new FeatureModelTokenType("FEATURENAME");
  IElementType INDENT = new FeatureModelTokenType("INDENT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FEATURE) {
        return new FeatureModelFeatureImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
