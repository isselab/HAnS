package se.ch.HAnS.featureModel.psi.impl;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.PsiFileFactoryImpl;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.FeatureModelLanguage;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FeatureModelPsiImplUtil {

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
        PsiFile f = PsiFileFactoryImpl.getInstance(
                feature.getProject()).createFileFromText(FeatureModelLanguage.INSTANCE, "Dummy\n" + newFeatureName);

        WriteCommandAction.runWriteCommandAction(feature.getProject(), () -> {
            feature.replace(f.getLastChild().copy());
        });

        return newFeatureName;
    }

    public static String addFeature(@NotNull FeatureModelFeature feature){
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

    private static String addToFeatureModel(@NotNull FeatureModelFeature feature, String newFeatureName) {
        PsiFile f = PsiFileFactoryImpl.getInstance(
                feature.getProject()).createFileFromText(
                        FeatureModelLanguage.INSTANCE, "Dummy\n" + "\t" + String.format("%1$"+(feature.getPrevSibling().getTextLength())+"s", "") + newFeatureName);
        WriteCommandAction.runWriteCommandAction(feature.getProject(), () -> {
            PsiElement [] elements = f.getChildren();
            feature.add(elements[elements.length-3].copy());
            feature.add(elements[elements.length-2].copy());
            feature.add(elements[elements.length-1].copy());
        });

        return newFeatureName;
    }

    public static int deleteFeature(@NotNull FeatureModelFeature feature){
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

    private static void deleteFromFeatureModelWithChildren(@NotNull FeatureModelFeature feature) {
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
