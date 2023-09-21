/*
Copyright 2021 Herman Jansson & Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.codeAnnotation.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationElementFactory;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationLpq;

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

    public static CodeAnnotationLpq setName(CodeAnnotationLpq element, String newName) {
        ASTNode featureNode = element.getNode();
        if (featureNode != null) {
            CodeAnnotationLpq feature = CodeAnnotationElementFactory.createLPQ(element.getProject(), newName);
            ASTNode newKeyNode = feature.getNode();
            element.getParent().getNode().replaceChild(featureNode, newKeyNode);
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
