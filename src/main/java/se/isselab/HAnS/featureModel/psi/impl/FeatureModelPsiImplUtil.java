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
package se.isselab.HAnS.featureModel.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.refactoring.rename.RenameDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;
import se.isselab.HAnS.referencing.FeatureReferenceUtil;

import javax.swing.*;
import java.util.*;
import java.util.regex.Pattern;

public class FeatureModelPsiImplUtil {

    public static ItemPresentation getPresentation(final FeatureModelFeature element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return element.getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return null;
            }
        };
    }

    // &begin[Referencing]
    public static String getFeatureName(FeatureModelFeature element){
        ASTNode featureNode = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
        if (featureNode != null) {
            // IMPORTANT: Convert embedded escaped spaces to simple spaces
            return featureNode.getText().replaceAll("\\\\ ", " ");
        } else {
            return null;
        }
    }

    public static String getName(FeatureModelFeature feature) {
        return feature.getFeatureName();
    }

    public static FeatureModelFeature setName(FeatureModelFeature element, String newName) {
        if (FeatureReferenceUtil.getOrigin() == element || FeatureReferenceUtil.getOrigin() == null) {
            FeatureReferenceUtil.getLPQ(element, newName);

            FeatureReferenceUtil.setElementsToRenameWhenRenaming(element, newName);

            ASTNode featureNode = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
            if (featureNode != null) {
                FeatureModelFeature feature = FeatureModelElementFactory.createFeature(element.getProject(), newName);
                ASTNode newKeyNode = feature.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
                if (newKeyNode != null)
                    element.getNode().replaceChild(featureNode, newKeyNode);
            }

            FeatureReferenceUtil.rename();

            FeatureReferenceUtil.reset();
        }
        else {
            ASTNode featureNode = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
            if (featureNode != null) {
                FeatureModelFeature feature = FeatureModelElementFactory.createFeature(element.getProject(), newName);
                ASTNode newKeyNode = feature.getFirstChild().getNode();
                element.getNode().replaceChild(featureNode, newKeyNode);
            }
        }
        return element;
    }

    private static List<PsiElement> getElementsToRename(FeatureModelFeature element, String newName) {
        List<PsiElement> elementsToRename= new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature){
                    if (((FeatureModelFeature) e).getLPQText().contains(newName)) {
                        elementsToRename.add(e);
                    }
                    else if (((FeatureModelFeature) e).getLPQText().contains(element.getNode().getText())) {
                        elementsToRename.add(e);
                    }
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

    public static PsiElement getNameIdentifier(FeatureModelFeature element) {
        ASTNode node = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
        if (node != null) {
            return node.getPsi();
        }
        return null;
    }
    // &end[Referencing]

    public static String getLPQText(PsiElement feature) {
        Deque<PsiElement> lpqStack = getLPQStack(feature);
        String lpq = null;

        if (lpqStack == null) {
            return null;
        }

        while (!lpqStack.isEmpty()) {
            PsiElement top = lpqStack.pop();
            if (lpq == null) {
                lpq = top.getText();
            }
            else {
                lpq = lpq.concat("::" + top.getText());
            }
        }

        return lpq;
    }

    public static Deque<PsiElement> getLPQStack(PsiElement feature) {
        List<Deque<PsiElement>> candidates = new ArrayList<>();

        feature.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeature
                        && element.getFirstChild().getText().equals(feature.getFirstChild().getText())){
                    Deque<PsiElement> stack = new ArrayDeque<>();
                    stack.add(element.getFirstChild());
                    candidates.add(stack);
                }
                super.visitElement(element);
            }
        });
        Deque<PsiElement> stack = new ArrayDeque<>();
        stack.add(feature.getFirstChild());

        return findLPQRecursively(candidates, stack);
    }

    private static Deque<PsiElement> findLPQRecursively(List<Deque<PsiElement>> candidates,Deque<PsiElement> feature) {
        if (candidates.size() == 1 || Objects.requireNonNull(feature.peek()).getParent().getParent() instanceof PsiFile) {
            return feature;
        }

        List<Deque<PsiElement>> remainingCandidates = new ArrayList<>();

        assert feature.peek() != null;
        PsiElement fParent = feature.peek().getParent().getParent().getFirstChild();

        for (Deque<PsiElement> c:candidates) {
            assert c.peek() != null;
            PsiElement cParent = c.peek().getParent().getParent().getFirstChild();
            if (cParent.getText().equals(fParent.getText())) {
                c.push(cParent);
                remainingCandidates.add(c);
            }
        }

        feature.push(fParent);

        return findLPQRecursively(remainingCandidates, feature);
    }

    public static void renameFeature(@NotNull FeatureModelFeature feature){
        RenameDialog dialog = new RenameDialog(feature.getProject(), feature, null, null);
        dialog.show();
    }

    public static void addFeature(@NotNull FeatureModelFeature feature){
        String newFeatureName;
        outer: while (true) {
            newFeatureName = Messages.showInputDialog("Enter name of new feature.",
                    "New Feature", null);
            if (newFeatureName == null) {
                return;
            }
            if ("".equals(newFeatureName.trim())) {
                Messages.showMessageDialog("Feature name cannot be empty.",
                        "Error", Messages.getErrorIcon());
                continue;
            }
            if (!Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]*", newFeatureName)) {
                Messages.showMessageDialog("Feature name incorrect",
                        "Error", Messages.getErrorIcon());
                continue;
            }
            else {
                PsiElement[] l = feature.getChildren();
                for (PsiElement e : l) {
                    if (Objects.requireNonNull(e.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText().equals(newFeatureName)) {
                        Messages.showMessageDialog("Feature \"" + newFeatureName + "\" already exists.",
                                "Error", Messages.getErrorIcon());
                        continue outer;
                    }
                }
            }
            addToFeatureModel(feature, newFeatureName);
            return;
        }
    }

    private static String addToFeatureModel(@NotNull FeatureModelFeature feature, String newFeatureName) {
        Document document = PsiDocumentManager.getInstance(feature.getProject()).getDocument(feature.getContainingFile());
        int offset = feature.getTextOffset() + Objects.requireNonNull(feature.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getTextLength();

        int indent;

        System.out.println(newFeatureName);

        if (feature.getParent() instanceof PsiFile) {
            indent = 4;
        }
        else if (feature.getPrevSibling() instanceof FeatureModelFeature) {
            indent = feature.getPrevSibling().getLastChild().getTextLength() + 4; // TODO: Make indentation setting dependent
        }
        else {
            indent = feature.getPrevSibling().getTextLength() + 4;
        }

        //FeatureReferenceUtil.setElementsToRenameWhenAdding(feature, newFeatureName);

        if (document != null) {
            String documentText = document.getText();
            String sub = documentText.substring(0, offset);
            String remainder = documentText.substring(offset);
            String newContent = sub.concat("\n" + String.format("%1$" + (indent) + "s", "") + newFeatureName).concat(remainder);
            Runnable r = () -> {
                document.setReadOnly(false);
                document.setText(newContent);
            };
            WriteCommandAction.runWriteCommandAction(feature.getProject(), r);
        }

        //FeatureReferenceUtil.rename();
        //FeatureReferenceUtil.reset();

        return newFeatureName;
    }

    public static int deleteFeature(@NotNull PsiElement feature){
        int response = Messages.showOkCancelDialog(
                "Are you sure you want to remove the feature from the list?",
                "Delete Feature",
                Messages.getOkButton(),
                Messages.getCancelButton(),
                Messages.getWarningIcon());
        if (response == 0) {
            deleteFromFeatureModelWithChildren(feature);
        }
        return 1;
    }

    private static void deleteFromFeatureModelWithChildren(@NotNull PsiElement feature) {
        Document document = PsiDocumentManager.getInstance(feature.getProject()).getDocument(feature.getContainingFile());
        int startOffset = feature.getTextOffset();
        int endOffset = feature.getTextOffset() + feature.getNode().getTextLength();

        if (document != null) {
            String documentText = document.getText();
            String sub = documentText.substring(0, startOffset);
            String remainder = documentText.substring(endOffset);
            String newContent = sub.concat(remainder);
            Runnable r = () -> {
                document.setReadOnly(false);
                document.setText(newContent);
            };
            WriteCommandAction.runWriteCommandAction(feature.getProject(), r);
        }
    }
}
