// This is a generated file. Not intended for manual editing.
package featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import featureModel.psi.impl.*;

public interface FeatureModelTypes {

  IElementType FEATURE = new FeatureModelElementType("FEATURE");
  IElementType PROJECT_NAME = new FeatureModelElementType("PROJECT_NAME");

  IElementType CRLF = new FeatureModelTokenType("CRLF");
  IElementType FEATURENAME = new FeatureModelTokenType("FEATURENAME");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FEATURE) {
        return new FeatureModelFeatureImpl(node);
      }
      else if (type == PROJECT_NAME) {
        return new FeatureModelProjectNameImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
