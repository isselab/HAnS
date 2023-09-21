// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.folderAnnotation.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.folderAnnotation.psi.impl.*;

public interface FolderAnnotationTypes {

  IElementType FEATURE = new FolderAnnotationElementType("FEATURE");
  IElementType LPQ = new FolderAnnotationElementType("LPQ");

  IElementType CRLF = new FolderAnnotationTokenType("CRLF");
  IElementType CS = new FolderAnnotationTokenType("CS");
  IElementType FEATURENAME = new FolderAnnotationTokenType("FEATURENAME");
  IElementType SEPARATOR = new FolderAnnotationTokenType("SEPARATOR");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FEATURE) {
        return new FolderAnnotationFeatureImpl(node);
      }
      else if (type == LPQ) {
        return new FolderAnnotationLpqImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
