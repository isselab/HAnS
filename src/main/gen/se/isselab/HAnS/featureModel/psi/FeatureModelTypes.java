// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.featureModel.psi.impl.*;

public interface FeatureModelTypes {

  IElementType COMPONENT = new FeatureModelElementType("COMPONENT");
  IElementType FEATURE = new FeatureModelElementType("FEATURE");
  IElementType LOGIC = new FeatureModelElementType("LOGIC");
  IElementType OR_BLOCK = new FeatureModelElementType("OR_BLOCK");
  IElementType SUB_LOGIC = new FeatureModelElementType("SUB_LOGIC");
  IElementType XOR_BLOCK = new FeatureModelElementType("XOR_BLOCK");

  IElementType CRLF = new FeatureModelTokenType("CRLF");
  IElementType DEDENT = new FeatureModelTokenType("DEDENT");
  IElementType FEATURENAME = new FeatureModelTokenType("FEATURENAME");
  IElementType INDENT = new FeatureModelTokenType("INDENT");
  IElementType OR = new FeatureModelTokenType("OR");
  IElementType QUESTIONMARK = new FeatureModelTokenType("QUESTIONMARK");
  IElementType XOR = new FeatureModelTokenType("XOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == COMPONENT) {
        return new FeatureModelComponentImpl(node);
      }
      else if (type == FEATURE) {
        return new FeatureModelFeatureImpl(node);
      }
      else if (type == LOGIC) {
        return new FeatureModelLogicImpl(node);
      }
      else if (type == OR_BLOCK) {
        return new FeatureModelOrBlockImpl(node);
      }
      else if (type == SUB_LOGIC) {
        return new FeatureModelSubLogicImpl(node);
      }
      else if (type == XOR_BLOCK) {
        return new FeatureModelXorBlockImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
