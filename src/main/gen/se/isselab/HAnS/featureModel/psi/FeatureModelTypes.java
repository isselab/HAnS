// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.featureModel.psi.impl.*;

public interface FeatureModelTypes {

  IElementType CROSS_CONSTRAINS = new FeatureModelElementType("CROSS_CONSTRAINS");
  IElementType FEATURE = new FeatureModelElementType("FEATURE");

  IElementType ARROW = new FeatureModelTokenType("ARROW");
  IElementType BRACKATSCLOSE = new FeatureModelTokenType("BRACKATSCLOSE");
  IElementType BRACKATSOPEN = new FeatureModelTokenType("BRACKATSOPEN");
  IElementType CRLF = new FeatureModelTokenType("CRLF");
  IElementType DEDENT = new FeatureModelTokenType("DEDENT");
  IElementType FEATURE1 = new FeatureModelTokenType("FEATURE1");
  IElementType FEATURENAME = new FeatureModelTokenType("FEATURENAME");
  IElementType INDENT = new FeatureModelTokenType("INDENT");
  IElementType OPTIONAL = new FeatureModelTokenType("OPTIONAL");
  IElementType OR = new FeatureModelTokenType("OR");
  IElementType XOR = new FeatureModelTokenType("XOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CROSS_CONSTRAINS) {
        return new FeatureModelCrossConstrainsImpl(node);
      }
      else if (type == FEATURE) {
        return new FeatureModelFeatureImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
