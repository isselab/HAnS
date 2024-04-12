// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.codeAnnotation.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import se.isselab.HAnS.codeAnnotation.psi.impl.*;

public interface CodeAnnotationTypes {

  IElementType BEGINMARKER = new CodeAnnotationElementType("BEGINMARKER");
  IElementType ENDMARKER = new CodeAnnotationElementType("ENDMARKER");
  IElementType FEATURE = new CodeAnnotationElementType("FEATURE");
  IElementType LINEMARKER = new CodeAnnotationElementType("LINEMARKER");
  IElementType LPQ = new CodeAnnotationElementType("LPQ");
  IElementType PARAMETER = new CodeAnnotationElementType("PARAMETER");

  IElementType BEGIN = new CodeAnnotationTokenType("BEGIN");
  IElementType CBRACKET = new CodeAnnotationTokenType("CBRACKET");
  IElementType COMMENTMARKER = new CodeAnnotationTokenType("COMMENTMARKER");
  IElementType CS = new CodeAnnotationTokenType("CS");
  IElementType END = new CodeAnnotationTokenType("END");
  IElementType FEATURENAME = new CodeAnnotationTokenType("FEATURENAME");
  IElementType LINE = new CodeAnnotationTokenType("LINE");
  IElementType OBRACKET = new CodeAnnotationTokenType("OBRACKET");
  IElementType SEPARATOR = new CodeAnnotationTokenType("SEPARATOR");
  IElementType SPACE = new CodeAnnotationTokenType("SPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == BEGINMARKER) {
        return new CodeAnnotationBeginmarkerImpl(node);
      }
      else if (type == ENDMARKER) {
        return new CodeAnnotationEndmarkerImpl(node);
      }
      else if (type == FEATURE) {
        return new CodeAnnotationFeatureImpl(node);
      }
      else if (type == LINEMARKER) {
        return new CodeAnnotationLinemarkerImpl(node);
      }
      else if (type == LPQ) {
        return new CodeAnnotationLpqImpl(node);
      }
      else if (type == PARAMETER) {
        return new CodeAnnotationParameterImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
