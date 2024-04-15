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
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.*;
import com.intellij.refactoring.rename.RenameDialog;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.CodeEditorModal;
import se.isselab.HAnS.FeatureAnnotationToDelete;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.*;
import se.isselab.HAnS.referencing.FeatureReferenceUtil;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
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

    private static FeatureModelFeature getFeatureFromLPQ(Project project, String lpq) {
        List<FeatureModelFeature> listOfFeatures = ReadAction.compute(() -> FeatureModelUtil.findLPQ(project, lpq));
        if (listOfFeatures.isEmpty()) { return null; }
        FeatureModelFeature feature = listOfFeatures.get(0);
        return feature;
    }

    // generates String with feature tree
    private static String generateTreeString(FeatureModelFeature feature, int level) {
        StringBuilder sb = new StringBuilder();

        // Indentation based on the level
        for (int i = 0; i < level * 4; i++) {
            sb.append(" ");
        }

        sb.append(feature.getName()).append("\n"); // add current feature name

        // Recursively process children with increased indentation level
        for (PsiElement child : feature.getChildren()) {
            sb.append(generateTreeString(((FeatureModelFeature)child), level + 1));
        }

        return sb.toString();
    }

    private static void generateListOfLpqs(FeatureModelFeature feature, List<String> lpqs) {
        lpqs.add(feature.getLPQText());
        for (PsiElement child : feature.getChildren()) {
            generateListOfLpqs(((FeatureModelFeature) child), lpqs);
        }
    }

    public static void moveFeatureWithChildren(@NotNull FeatureModelFeature parentFeature, @NotNull FeatureModelFeature childFeature) {
        final Project projectInstance = ReadAction.compute(parentFeature::getProject);

        // stores childFeature with children to perform rename of references
        FeatureReferenceUtil.setElementsToRenameAfterAddingWithChildren(childFeature);

        // stores old lpqs of childFeature with its kids to rename references
        List<String> oldLpqs = new ArrayList<>();
        generateListOfLpqs(childFeature, oldLpqs);

        // deletes lines containing childFeature and its kids
        childFeature.deleteFromFeatureModel();

        // updates the feature model file to avoid renaming deleted features
        PsiDocumentManager.getInstance(projectInstance).commitAllDocuments();

        // locates childFeature below parent
        addFeatureWithChildren(parentFeature, childFeature);

        // finds new element that corresponds to childFeature, since original childFeature no longer exists in Psi tree
        AtomicReference<FeatureModelFeature> newChild = new AtomicReference<>();
        Arrays.stream(parentFeature.getChildren()).forEach(child -> {
            if(((FeatureModelFeature) child).getLPQText().contains(childFeature.getFeatureName())) {
                newChild.set((FeatureModelFeature) child);
            }
        });
        List<String> newLpqs = new ArrayList<>();
        generateListOfLpqs(newChild.get(), newLpqs);

        // renames features of child element tree
        FeatureReferenceUtil.updateChildAfterAdded(oldLpqs, newLpqs);

        FeatureReferenceUtil.reset();

    }

    public static void addFeatureWithChildren(@NotNull FeatureModelFeature parentFeature, @NotNull FeatureModelFeature childFeature) {
        Project projectInstance = ReadAction.compute(parentFeature::getProject);

        Document document = PsiDocumentManager.getInstance(projectInstance).getDocument(parentFeature.getContainingFile());
        if (document != null) {
            PsiElement prevSibling = parentFeature.getPrevSibling();
            int indent;
            // if root feature -> 4
            // otherwise indent of parentFeature + 4
            if (prevSibling instanceof PsiFile) {
                indent = 4;
            } else {
                int parentOffset = parentFeature.getTextOffset();
                int parentLineOffset = document.getLineStartOffset(document.getLineNumber(parentOffset));
                indent = (parentOffset - parentLineOffset) + 4;
            }
            int level = indent / 4;

            // create string representation of child feature with its children and
            // append it to .feature-model file right after parent feature
            String result = generateTreeString(childFeature, level);

            int lineStartOffset = document.getLineStartOffset(document.getLineNumber(parentFeature.getTextOffset()) + 1);
            String beforeLine = document.getText().substring(0, lineStartOffset);
            String remainder = document.getText().substring(lineStartOffset);
            String newString = beforeLine.concat(result).concat(remainder);

            document.setReadOnly(false);
            document.setText(newString);

            // update features outside childFeature
            String newName = childFeature.getName();
            FeatureReferenceUtil.getLPQ(parentFeature, newName);
            FeatureReferenceUtil.setElementsToRenameWhenAdding(parentFeature, newName);

            PsiDocumentManager.getInstance(projectInstance).commitAllDocuments();

            FeatureReferenceUtil.rename();
            FeatureReferenceUtil.reset();
        }
    }


    public static String addFeatureToFeatureModel(@NotNull FeatureModelFeature element, String newName) {
        FeatureModelPsiImplUtil.addToFeatureModel(element, newName);

        FeatureReferenceUtil.getLPQ(element, newName);
        FeatureReferenceUtil.setElementsToRenameWhenAdding(element, newName);

        final Project projectInstance = ReadAction.compute(element::getProject);

        PsiDocumentManager.getInstance(projectInstance).commitAllDocuments();

        FeatureReferenceUtil.rename();
        FeatureReferenceUtil.reset();
        return newName;
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
        return newFeatureName;
    }

    public static FeatureModelFeature deleteFromFeatureModel(@NotNull PsiElement feature) {
        Project projectInstance = feature.getProject();
        Document document = PsiDocumentManager.getInstance(projectInstance).getDocument(feature.getContainingFile());
        if (document!= null) {
            int lineStartOffset = document.getLineStartOffset(document.getLineNumber(feature.getTextOffset()));
            ASTNode featureNode = feature.getNode();
            int lineEndOffset = document.getLineEndOffset(document.getLineNumber(feature.getTextOffset() + featureNode.getTextLength())-1);
            Runnable r = () -> {
                document.deleteString(lineStartOffset, lineEndOffset+1);
            };
            WriteCommandAction.runWriteCommandAction(feature.getProject(), r);

            return (FeatureModelFeature) feature;
        }
        return null;
    }
    public static FeatureModelFeature deleteFeatureWithAnnotations(@NotNull PsiElement feature) {
        Project projectInstance = feature.getProject();
        Document document = PsiDocumentManager.getInstance(projectInstance).getDocument(feature.getContainingFile());
        if (document!= null) {
            FeatureReferenceUtil.setElementsToDelete((FeatureModelFeature) feature);
            FeatureReferenceUtil.setElementsToRenameWhenDeleting((FeatureModelFeature) feature);
            FeatureReferenceUtil.delete();

            deleteFromFeatureModel(feature);

            PsiDocumentManager.getInstance(projectInstance).commitAllDocuments();

            FeatureReferenceUtil.rename();
            FeatureReferenceUtil.reset();
            return (FeatureModelFeature) feature;
        }
        return null;
    }

    public static boolean deleteFeatureWithCode(@NotNull PsiElement feature) {
        Project projectInstance = feature.getProject();
        Document document = PsiDocumentManager.getInstance(projectInstance).getDocument(feature.getContainingFile());
        if (document!= null) {
            Set<FeatureAnnotationToDelete> annotations = FeatureReferenceUtil.setElementsToDropWhenDeletingMain((FeatureModelFeature) feature);
            if (!annotations.isEmpty()) {
                CodeEditorModal modal = new CodeEditorModal(feature.getProject());
                modal.setFileList(annotations);

                boolean exitCode = modal.showAndGet();
                PsiDocumentManager.getInstance(projectInstance).commitAllDocuments();
                if (exitCode) { // if OK was clicked try again
                    deleteFeatureWithCode(feature);
                } else {
                    modal.disposeIfNeeded();
                    return false;
                }
            } else { // if no tangled features are present
                FeatureReferenceUtil.setElementsToRenameWhenDeleting((FeatureModelFeature) feature);
                FeatureReferenceUtil.setMapToDeleteWithCode((FeatureModelFeature) feature);

                FeatureReferenceUtil.deleteWithCode();
                deleteFromFeatureModel(feature);

                PsiDocumentManager.getInstance(projectInstance).commitAllDocuments();

                FeatureReferenceUtil.rename();
                FeatureReferenceUtil.reset();
                return true;
            }
        }
        return false;
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
        if (document != null) {
            int lineStartOffset = document.getLineStartOffset(document.getLineNumber(feature.getTextOffset()));
            int lineEndOffset = document.getLineEndOffset(document.getLineNumber(feature.getTextOffset())) + 1;
            Runnable r = () -> {
                document.deleteString(lineStartOffset, lineEndOffset);
            };
            WriteCommandAction.runWriteCommandAction(feature.getProject(), r);
        }
    }
}
