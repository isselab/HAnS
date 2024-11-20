package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelPsiImplUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FindChildrenAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        //System.out.println("Test");
        // Get the current PSI file

        Project project = anActionEvent.getProject();
        if (project == null) {
            System.out.println("No project found.");
            return;
        }

        // Get the active editor
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            System.out.println("No editor found. Ensure you are working in a valid editor context.");
            return;
        }

        // Get caret offset
        int offset = editor.getCaretModel().getOffset();
        System.out.println("Caret offset: " + offset);

        // Get the PSI file in the current editor
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile != null) {
            // Process the file currently open in the editor
            printFeatureNameInFile(psiFile, offset);
        } else {
            System.out.println("No PSI file found in editor. Searching across the project.");
            // If no file is open, search across the entire project
            searchInProject(project);
        }
    }

    private void printFeatureNameInFile(PsiFile psiFile, int offset) {
        // Find the element at the caret position in the current file
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        if (elementAtCaret == null) {
            System.out.println("No PSI element found at caret position.");
            return;
        }
        System.out.println("Element at caret: " + elementAtCaret.getClass().getSimpleName());

        if (elementAtCaret instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) elementAtCaret;
            String featureName = feature.getFeatureName();
            if (featureName != null && !featureName.isEmpty()) {
                System.out.println("Selected feature name: " + featureName);
            } else {
                System.out.println("Feature name is empty or null.");
            }
        } else {
            System.out.println("Element at caret is not a FeatureModelFeature.");
        }
    }

    /*private void searchInProject(Project project) {
        // Get the project scope for searching
        GlobalSearchScope searchScope = ProjectScope.getAllScope(project);

        // Get all files in the project
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile[] psiFiles = (PsiFile[]) psiManager.getProject().getBaseDir().getChildren();

        for (PsiFile psiFile : psiFiles) {
            if (psiFile.getVirtualFile() == null) continue;
            if (!searchScope.contains(psiFile.getVirtualFile())) continue;

            // Search through the file for any FeatureModelFeature
            System.out.println("Scanning file: " + psiFile.getName());
            PsiElement[] featureElements = PsiTreeUtil.collectElementsOfType(psiFile, FeatureModelFeature.class);

            for (PsiElement element : featureElements) {
                FeatureModelFeature feature = (FeatureModelFeature) element;
                String featureName = feature.getFeatureName();
                if (featureName != null && !featureName.isEmpty()) {
                    System.out.println("Feature name found: " + featureName);
                }
            }
        }
        *//*PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            System.out.println("No PSI file found. Ensure a file is open and try again.");
            return;
        }

        // Get the active editor
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            System.out.println("No editor found. Ensure you are working in a valid editor context.");
            return;
        }

        // Get caret offset
        int offset = editor.getCaretModel().getOffset();
        System.out.println("Caret offset: " + offset);

        // Find the element at the caret position
        PsiElement elementAtCaret = psiFile.findElementAt(offset);
        if (elementAtCaret == null) {
            System.out.println("No PSI element found at caret position.");
            return;
        }
        System.out.println("Element at caret: " + elementAtCaret.getClass().getSimpleName());
*//*
        // Check if the element is a FeatureModelFeature
        *//*if (elementAtCaret instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) elementAtCaret;
            String featureName = feature.getFeatureName();
            if (featureName != null && !featureName.isEmpty()) {
                System.out.println("Selected feature name: " + featureName);
            } else {
                System.out.println("Feature name is empty or null.");
            }
        } else {
            System.out.println("Element at caret is not a FeatureModelFeature.");
        }*//*

        // Also try using LangDataKeys.PSI_ELEMENT
        PsiElement langElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (langElement == null) {
            System.out.println("LangDataKeys.PSI_ELEMENT is null.");
        } else if (langElement instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) langElement;
            String featureName = feature.getFeatureName();
            if (featureName != null && !featureName.isEmpty()) {
                System.out.println("Selected feature name (via LangDataKeys): " + featureName);
            } else {
                System.out.println("Feature name (via LangDataKeys) is empty or null.");
            }
        } else {
            System.out.println("LangDataKeys.PSI_ELEMENT is not a FeatureModelFeature.");
        }


        *//*PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE); // to find the method




            Project project = anActionEvent.getProject(); // get the current project so that annotation class can work on it
            Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR); // get the active editor to find the caret position
            PsiElement editor1= anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
            if (psiFile == null)
                return;
            assert editor != null;
            int offset = editor.getCaretModel().getOffset();
        if (editor1 instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) editor1;
            if (feature != null) {
                String featureName = feature.getFeatureName();
                System.out.println("Selected feature name: " + featureName);
            }
            else {
                System.out.println("Feature name not found.");
            }
        }*//*



            *//*FeatureModelFeature elementAtCaret = (FeatureModelFeature)psiFile.findElementAt(offset); // find the element at the current caret position
            if (elementAtCaret != null)
            {
                FeatureModelPsiImplUtil featureModelPsiImplUtil = new FeatureModelPsiImplUtil();
                if (elementAtCaret instanceof FeatureModelFeature) {
                    // Cast to FeatureModelFeature and get the name
                    FeatureModelFeature feature = (FeatureModelFeature) elementAtCaret;
                    String featureName = FeatureModelPsiImplUtil.getFeatureName(feature);

                    if (featureName != null) {
                        // Do something with the feature name
                        System.out.println("Selected feature name: " + featureName);
                    } else {
                        System.out.println("Feature name not found.");
                    }
                } else {
                    System.out.println("No feature selected at the caret.");
                }*//*
        //This method starts at the given PSI element (elementAtCaret) and traverses upwards through the PSI tree
        // (moving towards the root), looking for an element of the specified type, in this case, a PsiMethod.
                 *//*psiMethod = PsiTreeUtil.getParentOfType(elementAtCaret,PsiMethod.class);

                assert project != null;

                JavaPsiFacade.getElementFactory(project).createAnnotationFromText(textinput, psiMethod);

                //method returns a PsiElementFactory associated with the given project, allowing you to create new Java-related PSI elements
                // (like annotations, classes, methods, etc.) for that specific project
                PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
                // creates an annotation
                PsiAnnotation annotation = elementFactory.createAnnotationFromText(textinput, psiMethod);

                //writes an annotation before the function
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    assert psiMethod != null;
                    psiMethod.getModifierList().addBefore(annotation, psiMethod.getModifierList().getFirstChild());
                });*//*

    }*/
    private void searchInProject(Project project) {
        // Get the project scope for searching
        GlobalSearchScope searchScope = ProjectScope.getAllScope(project);

        // Get all files in the project
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile[] psiFiles = (PsiFile[]) psiManager.getProject().getBaseDir().getChildren();

        for (PsiFile psiFile : psiFiles) {
            if (psiFile.getVirtualFile() == null) continue;
            if (!searchScope.contains(psiFile.getVirtualFile())) continue;

            // Search through the file for any FeatureModelFeature
            System.out.println("Scanning file: " + psiFile.getName());

            // Collecting all instances of FeatureModelFeature from the file
            Collection<FeatureModelFeature> featureElements = PsiTreeUtil.collectElementsOfType(psiFile, FeatureModelFeature.class);

            // Converting Collection to PsiElement array
            PsiElement[] featureElementsArray = featureElements.toArray(new PsiElement[0]);

            for (PsiElement element : featureElementsArray) {
                FeatureModelFeature feature = (FeatureModelFeature) element;
                String featureName = feature.getFeatureName();
                if (featureName != null && !featureName.isEmpty()) {
                    System.out.println("Feature name found: " + featureName);
                }
            }

}
    }
}



