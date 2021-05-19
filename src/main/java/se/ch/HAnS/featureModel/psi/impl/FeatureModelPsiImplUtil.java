package se.ch.HAnS.featureModel.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.FeatureModelLanguage;
import se.ch.HAnS.featureModel.FeatureModelUtil;
import se.ch.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import se.ch.HAnS.referencing.FeatureReferenceUtil;

import java.util.*;
import java.util.regex.Pattern;

public class FeatureModelPsiImplUtil {

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

            FeatureReferenceUtil.setElementsToRename(element, newName);

            ASTNode featureNode = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
            if (featureNode != null) {
                FeatureModelFeature feature = FeatureModelElementFactory.createFeature(element.getProject(), newName);
                ASTNode newKeyNode = feature.getFirstChild().getNode();
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

    public static String renameFeature(@NotNull FeatureModelFeature feature){
        String newFeatureName;
        while (true) {
            newFeatureName = Messages.showInputDialog("Enter new name",
                    "Rename Feature", null);
            if (newFeatureName == null) {
                return null;
            }
            if ("".equals(newFeatureName.trim())) {
                Messages.showMessageDialog("Feature name cannot be empty",
                        "Error", Messages.getErrorIcon());
                continue;
            }
            if (!Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]+", newFeatureName)) {
                Messages.showMessageDialog("Feature name incorrect",
                        "Error", Messages.getErrorIcon());
                continue;
            }
            return renameInFeatureModel(feature, newFeatureName);
        }
    }

    private static String renameInFeatureModel(@NotNull FeatureModelFeature feature, String newFeatureName) {
        for (PsiReference reference : ReferencesSearch.search(feature)) {
            reference.handleElementRename(newFeatureName);
        }

        feature.setName(newFeatureName);

        return newFeatureName;
    }

    public static String addFeature(@NotNull PsiElement feature){
        String newFeatureName;
        while (true) {
            newFeatureName = Messages.showInputDialog("Enter name of new feature",
                    "New Feature", null);
            if (newFeatureName == null) {
                return null;
            }
            if ("".equals(newFeatureName.trim())) {
                Messages.showMessageDialog("Feature name cannot be empty",
                        "Error", Messages.getErrorIcon());
                continue;
            }
            if (!Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]*", newFeatureName)) {
                Messages.showMessageDialog("Feature name incorrect",
                        "Error", Messages.getErrorIcon());
                continue;
            }
            return addToFeatureModel(feature, newFeatureName);
        }
    }

    private static String addToFeatureModel(@NotNull PsiElement feature, String newFeatureName) {
        PsiFile f = PsiFileFactoryImpl.getInstance(
                feature.getProject()).createFileFromText(
                        FeatureModelLanguage.INSTANCE, "Dummy\n" + String.format("%1$"+(feature.getPrevSibling().getTextLength() + 4)+"s", "") + newFeatureName);
        WriteCommandAction.runWriteCommandAction(feature.getProject(), () -> {
            PsiElement [] elements = f.getChildren();
            feature.add(elements[elements.length-3]);
            feature.add(elements[elements.length-2]);
            feature.add(elements[elements.length-1]);
        });
        f.clearCaches();

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
        List<PsiElement> toDelete = new ArrayList<>();

        feature.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            boolean add = false;
            int indentation;

            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeature){
                    if (add) {
                        if (element.getPrevSibling().getText().length() <= indentation) {
                            add = false;
                        }
                        else {
                            deleteLine(element);
                        }
                    }
                    else if (element.equals(feature)){
                        deleteLine(element);
                        indentation = element.getPrevSibling().getText().length();
                        add = true;
                    }
                }
                super.visitElement(element);
            }

            private void deleteLine(@NotNull PsiElement element) {
                toDelete.add(element);
                toDelete.add(element.getPrevSibling());
                toDelete.add(element.getPrevSibling().getPrevSibling());
            }
        });

        WriteCommandAction.runWriteCommandAction(feature.getProject(), () -> {
            for (PsiElement e : toDelete) {
                e.delete();
            }
        });
    }
}
