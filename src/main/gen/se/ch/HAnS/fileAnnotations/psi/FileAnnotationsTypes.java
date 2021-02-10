// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.fileAnnotations.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.ch.HAnS.fileAnnotations.psi.impl.*;

public interface FileAnnotationsTypes {

  IElementType FEATURE_NAME = new FileAnnotationsElementType("FEATURE_NAME");
  IElementType FILE_ANNOTATION = new FileAnnotationsElementType("FILE_ANNOTATION");
  IElementType FILE_NAME = new FileAnnotationsElementType("FILE_NAME");
  IElementType FILE_REFERENCE = new FileAnnotationsElementType("FILE_REFERENCE");
  IElementType FILE_REFERENCES = new FileAnnotationsElementType("FILE_REFERENCES");
  IElementType LPQ = new FileAnnotationsElementType("LPQ");
  IElementType LPQ_REFERENCES = new FileAnnotationsElementType("LPQ_REFERENCES");

  IElementType CM = new FileAnnotationsTokenType("CM");
  IElementType COLON = new FileAnnotationsTokenType("COLON");
  IElementType CS = new FileAnnotationsTokenType("CS");
  IElementType DOT = new FileAnnotationsTokenType("DOT");
  IElementType NEWLINE = new FileAnnotationsTokenType("NEWLINE");
  IElementType SEPARATOR = new FileAnnotationsTokenType("SEPARATOR");
  IElementType SPACE = new FileAnnotationsTokenType("SPACE");
  IElementType STRING = new FileAnnotationsTokenType("STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FEATURE_NAME) {
        return new FileAnnotationsFeatureNameImpl(node);
      }
      else if (type == FILE_ANNOTATION) {
        return new FileAnnotationsFileAnnotationImpl(node);
      }
      else if (type == FILE_NAME) {
        return new FileAnnotationsFileNameImpl(node);
      }
      else if (type == FILE_REFERENCE) {
        return new FileAnnotationsFileReferenceImpl(node);
      }
      else if (type == FILE_REFERENCES) {
        return new FileAnnotationsFileReferencesImpl(node);
      }
      else if (type == LPQ) {
        return new FileAnnotationsLpqImpl(node);
      }
      else if (type == LPQ_REFERENCES) {
        return new FileAnnotationsLpqReferencesImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
