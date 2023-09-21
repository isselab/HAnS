// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.fileAnnotation.psi.impl.*;

public interface FileAnnotationTypes {

  IElementType FEATURE_NAME = new FileAnnotationElementType("FEATURE_NAME");
  IElementType FILE_ANNOTATION = new FileAnnotationElementType("FILE_ANNOTATION");
  IElementType FILE_NAME = new FileAnnotationElementType("FILE_NAME");
  IElementType FILE_REFERENCE = new FileAnnotationElementType("FILE_REFERENCE");
  IElementType FILE_REFERENCES = new FileAnnotationElementType("FILE_REFERENCES");
  IElementType LPQ = new FileAnnotationElementType("LPQ");
  IElementType LPQ_REFERENCES = new FileAnnotationElementType("LPQ_REFERENCES");

  IElementType CM = new FileAnnotationTokenType("CM");
  IElementType COLON = new FileAnnotationTokenType("COLON");
  IElementType CS = new FileAnnotationTokenType("CS");
  IElementType DOT = new FileAnnotationTokenType("DOT");
  IElementType NEWLINE = new FileAnnotationTokenType("NEWLINE");
  IElementType SEPARATOR = new FileAnnotationTokenType("SEPARATOR");
  IElementType SPACE = new FileAnnotationTokenType("SPACE");
  IElementType STRING = new FileAnnotationTokenType("STRING");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == FEATURE_NAME) {
        return new FileAnnotationFeatureNameImpl(node);
      }
      else if (type == FILE_ANNOTATION) {
        return new FileAnnotationFileAnnotationImpl(node);
      }
      else if (type == FILE_NAME) {
        return new FileAnnotationFileNameImpl(node);
      }
      else if (type == FILE_REFERENCE) {
        return new FileAnnotationFileReferenceImpl(node);
      }
      else if (type == FILE_REFERENCES) {
        return new FileAnnotationFileReferencesImpl(node);
      }
      else if (type == LPQ) {
        return new FileAnnotationLpqImpl(node);
      }
      else if (type == LPQ_REFERENCES) {
        return new FileAnnotationLpqReferencesImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
