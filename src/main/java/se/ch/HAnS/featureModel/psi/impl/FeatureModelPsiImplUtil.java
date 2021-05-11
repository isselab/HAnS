package se.ch.HAnS.featureModel.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.FeatureModelLanguage;
import se.ch.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class FeatureModelPsiImplUtil {

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

    public static PsiElement setName(FeatureModelFeature element, String newName) {
        ASTNode featureNode = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
        if (featureNode != null) {
            FeatureModelFeature feature = FeatureModelElementFactory.createFeature(element.getProject(), newName);
            ASTNode newKeyNode = feature.getFirstChild().getNode();
            element.getNode().replaceChild(featureNode, newKeyNode);
        }
        return element;
    }

    public static PsiElement getNameIdentifier(FeatureModelFeature element) {
        ASTNode node = element.getNode().findChildByType(FeatureModelTypes.FEATURENAME);
        if (node != null) {
            /*FeatureModelFeature feature = (FeatureModelFeature) node.getPsi();
            String name = "dummy.feature-to-folder";
            FolderAnnotationFile f = (FolderAnnotationFile) PsiFileFactory.getInstance(element.getProject()).
                    createFileFromText(name, FolderAnnotationFileType.INSTANCE, feature.getLPQ());
            return f.getFirstChild();
            */
            return node.getPsi();
        }
        return null;
    }

    public static String getLPQ(PsiElement feature) {
        List<Deque<PsiElement>> candidates = new ArrayList<>();

        feature.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeatureImpl
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

        Deque<PsiElement> result = findLPQRecursively(candidates, stack);

        String lpq = null;

        while (!result.isEmpty()) {
            PsiElement top = result.pollLast();
            if (lpq == null) {
                lpq = top.getText();
            }
            else {
                lpq = lpq.concat("::" + top.getText());
            }
        }

        return lpq;
    }

    private static Deque<PsiElement> findLPQRecursively(List<Deque<PsiElement>> candidates, Deque<PsiElement> feature) {
        if (candidates.size() == 1) {
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

        feature.add(fParent);

        return findLPQRecursively(remainingCandidates, feature);
    }

    public static String renameFeature(@NotNull PsiElement feature){
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

    private static String renameInFeatureModel(@NotNull PsiElement feature, String newFeatureName) {
        PsiFile f = PsiFileFactoryImpl.getInstance(
                feature.getProject()).createFileFromText(FeatureModelLanguage.INSTANCE, newFeatureName);

        WriteCommandAction.runWriteCommandAction(feature.getProject(), () -> {
            feature.replace(f.getLastChild().copy());
        });

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
                if (element instanceof FeatureModelFeatureImpl ){
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
