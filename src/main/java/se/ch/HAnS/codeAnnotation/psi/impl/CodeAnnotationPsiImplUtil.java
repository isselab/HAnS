package se.ch.HAnS.codeAnnotation.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationElementFactory;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationLpq;

public class CodeAnnotationPsiImplUtil {

    // &begin[Referencing]
    public static String getName(CodeAnnotationLpq feature) {
        ASTNode featureNode = feature.getNode();
        if (featureNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return featureNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static PsiElement setName(CodeAnnotationLpq element, String newName) {
        ASTNode featureNode = element.getNode();
        if (featureNode != null) {
            CodeAnnotationLpq feature = CodeAnnotationElementFactory.createLPQ(element.getProject(), newName);
            ASTNode newKeyNode = feature.getFirstChild().getNode();
            element.getNode().replaceChild(featureNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(CodeAnnotationLpq element) {
        ASTNode node = element.getNode();
        if (node != null) {
            return node.getPsi();
        }
        return null;
    }
    // &end[Referencing]

}
