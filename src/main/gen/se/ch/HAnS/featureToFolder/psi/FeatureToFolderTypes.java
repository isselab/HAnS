// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.featureToFolder.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.ch.HAnS.featureToFolder.psi.impl.*;

public interface FeatureToFolderTypes {

  IElementType LPQ = new FeatureToFolderElementType("LPQ");

  IElementType CRLF = new FeatureToFolderTokenType("CRLF");
  IElementType FEATURENAME = new FeatureToFolderTokenType("FEATURENAME");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == LPQ) {
        return new FeatureToFolderLpqImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
