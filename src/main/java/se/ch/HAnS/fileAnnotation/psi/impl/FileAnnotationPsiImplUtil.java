package se.ch.HAnS.fileAnnotation.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationElementFactory;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFileReference;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationElementFactory;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;

public class FileAnnotationPsiImplUtil {

    //&begin[Referencing]
    public static String getName(FileAnnotationLpq feature) {
        ASTNode featureNode = feature.getNode();
        if (featureNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return featureNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static FileAnnotationLpq setName(FileAnnotationLpq element, String newName) {
        ASTNode featureNode = element.getNode();
        if (featureNode != null) {
            FileAnnotationLpq feature = FileAnnotationElementFactory.createLPQ(element.getProject(), newName);
            ASTNode newKeyNode = feature.getNode();
            element.getParent().getNode().replaceChild(featureNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(FileAnnotationLpq element) {
        ASTNode node = element.getNode();
        if (node != null) {
            return node.getPsi();
        }
        return null;
    }
    //&end[Referencing]

}
