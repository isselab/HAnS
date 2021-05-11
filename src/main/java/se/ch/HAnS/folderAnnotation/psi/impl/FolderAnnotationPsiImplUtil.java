package se.ch.HAnS.folderAnnotation.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationElementFactory;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;

public class FolderAnnotationPsiImplUtil {

    public static String getName(FolderAnnotationLpq feature) {
        ASTNode featureNode = feature.getNode();
        if (featureNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return featureNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static PsiElement setName(FolderAnnotationLpq element, String newName) {
        ASTNode featureNode = element.getNode();
        if (featureNode != null) {
            FolderAnnotationLpq feature = FolderAnnotationElementFactory.createLPQ(element.getProject(), newName);
            ASTNode newKeyNode = feature.getFirstChild().getNode();
            element.getNode().replaceChild(featureNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(FolderAnnotationLpq element) {
        ASTNode node = element.getNode();
        if (node != null) {
            return node.getPsi();
        }
        return null;
    }

}
