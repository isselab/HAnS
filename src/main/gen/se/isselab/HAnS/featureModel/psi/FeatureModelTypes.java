// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.featureModel.psi.impl.*;

public interface FeatureModelTypes {

  IElementType BOOLEAN_EXPRESSION = new FeatureModelElementType("BOOLEAN_EXPRESSION");
  IElementType CONSTRAINT = new FeatureModelElementType("CONSTRAINT");
  IElementType FEATURE = new FeatureModelElementType("FEATURE");
  IElementType FEATURE_OR_GROUP = new FeatureModelElementType("FEATURE_OR_GROUP");
  IElementType OPTIONALITY = new FeatureModelElementType("OPTIONALITY");
  IElementType OR_GROUP = new FeatureModelElementType("OR_GROUP");
  IElementType XOR_GROUP = new FeatureModelElementType("XOR_GROUP");

  IElementType AND = new FeatureModelTokenType("&&");
  IElementType CRLF = new FeatureModelTokenType("crlf");
  IElementType DEDENT = new FeatureModelTokenType("dedent");
  IElementType FEATURENAME = new FeatureModelTokenType("id");
  IElementType IMPLIES = new FeatureModelTokenType("=>");
  IElementType INDENT = new FeatureModelTokenType("indent");
  IElementType LBRACKET = new FeatureModelTokenType("[");
  IElementType LPAREN = new FeatureModelTokenType("(");
  IElementType NOT = new FeatureModelTokenType("!");
  IElementType OPTIONALITY_TOKEN = new FeatureModelTokenType("?");
  IElementType OR = new FeatureModelTokenType("or");
  IElementType OR_OP = new FeatureModelTokenType("||");
  IElementType RBRACKET = new FeatureModelTokenType("]");
  IElementType RPAREN = new FeatureModelTokenType(")");
  IElementType XOR = new FeatureModelTokenType("xor");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BOOLEAN_EXPRESSION) {
        return new FeatureModelBooleanExpressionImpl(node);
      }
      else if (type == CONSTRAINT) {
        return new FeatureModelConstraintImpl(node);
      }
      else if (type == FEATURE) {
        return new FeatureModelFeatureImpl(node);
      }
      else if (type == FEATURE_OR_GROUP) {
        return new FeatureModelFeatureOrGroupImpl(node);
      }
      else if (type == OPTIONALITY) {
        return new FeatureModelOptionalityImpl(node);
      }
      else if (type == OR_GROUP) {
        return new FeatureModelOrGroupImpl(node);
      }
      else if (type == XOR_GROUP) {
        return new FeatureModelXorGroupImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
