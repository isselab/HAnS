// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.featureModel.psi.impl.*;

public interface FeatureModelTypes {

  IElementType FEATURE = new FeatureModelElementType("FEATURE");
  IElementType GROUP_MODIFIER = new FeatureModelElementType("GROUP_MODIFIER");
  IElementType OPTIONAL_MODIFIER = new FeatureModelElementType("OPTIONAL_MODIFIER");

  IElementType CRLF = new FeatureModelTokenType("CRLF");
  IElementType DEDENT = new FeatureModelTokenType("DEDENT");
  IElementType FEATURENAME = new FeatureModelTokenType("FEATURENAME");
  IElementType INDENT = new FeatureModelTokenType("INDENT");
  IElementType OPTIONAL = new FeatureModelTokenType("OPTIONAL");
  IElementType OR = new FeatureModelTokenType("OR");
  IElementType XOR = new FeatureModelTokenType("XOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FEATURE) {
        return new FeatureModelFeatureImpl(node);
      }
      else if (type == GROUP_MODIFIER) {
        return new FeatureModelGroupModifierImpl(node);
      }
      else if (type == OPTIONAL_MODIFIER) {
        return new FeatureModelOptionalModifierImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
