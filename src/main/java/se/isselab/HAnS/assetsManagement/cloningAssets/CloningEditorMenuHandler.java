package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationFeatureImpl;

import java.util.ArrayList;
import java.util.List;

public class CloningEditorMenuHandler {

    public static void handleEditorMenu(AnActionEvent anActionEvent, Editor editor, TracingHandler tracingHandler){
        //Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile psiFile = PsiDocumentManager.getInstance(anActionEvent.getProject()).getPsiFile(editor.getDocument());
        if (psiFile == null) return;
        SelectionModel selectionModel = editor.getSelectionModel();
        if(selectionModel.hasSelection()){
            getHighlightedBlock(selectionModel, psiFile);
            tracingHandler.createCodeAssetsTrace();
            return;
        }
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(caretOffset);
        PsiMethod methodAtCaret = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if(methodAtCaret != null){
            cloneMethod(methodAtCaret);
        } else {
            cloneClass(element);
        }
        AssetsToClone.subAssetTrace = tracingHandler.createCodeAssetsTrace();
        tracingHandler.createFeatureTraces();
    }

    private static void cloneClass(PsiElement element) {
        PsiClass classAtCaret = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        AssetsToClone.clonedClass = classAtCaret;
        saveExtractedFeatureAnnotations(classAtCaret);
    }

    private static void cloneMethod(PsiMethod methodAtCaret) {
        AssetsToClone.clonedMethod = methodAtCaret;
        saveExtractedFeatureAnnotations(methodAtCaret);
    }

    private static void saveExtractedFeatureAnnotations(PsiElement element){
        var featuresAnnotated = extractFeatureNames(element);
        if(featuresAnnotated != null )
            FeaturesCodeAnnotations.getInstance().setFeatureNames(featuresAnnotated);
    }

    private static void getHighlightedBlock(SelectionModel selectionModel, PsiFile psiFile){
        // Get start and end offsets of the selected text
        int startOffset = selectionModel.getSelectionStart();
        int endOffset = selectionModel.getSelectionEnd();
        // Get the PSI elements corresponding to these offsets
        PsiElement startElement = psiFile.findElementAt(startOffset);
        PsiElement endElement = psiFile.findElementAt(endOffset);
        // Create a list to hold all PsiElements within the range
        List<PsiElement> elementsInRange = new ArrayList<>();
        // Traverse from startElement to endElement and collect PsiElements
        PsiElement currentElement = startElement;
        while (currentElement != null && !currentElement.equals(endElement)) {
            elementsInRange.add(currentElement);
            currentElement = PsiTreeUtil.nextLeaf(currentElement);
        }
        // Add the endElement to the list if it's not null
        if (endElement != null) {
            elementsInRange.add(endElement);
        }
        if(startElement != null && endElement != null){
            cloneBlock(elementsInRange);
        }

    }

    private static void cloneBlock(List<PsiElement> elements) {
        AssetsToClone.elementsInRange = elements;
        FeaturesCodeAnnotations.getInstance().setFeatureNames(extractFeaturesOfCodeBlock(elements));
    }

    private static List<String> extractFeatureNames(PsiElement elements) {
        List<String> featureNames = new ArrayList<>();

        for (PsiElement element : PsiTreeUtil.findChildrenOfType(elements, PsiElement.class)) {
            if (element instanceof PsiComment) {
                if (element instanceof PsiLanguageInjectionHost) {
                    PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) element;
                    InjectedLanguageManager manager = InjectedLanguageManager.getInstance(host.getProject());
                    List<PsiElement> injectedElements = new ArrayList<>();
                    manager.enumerate(host, (injectedPsi, places) -> {
                        injectedElements.addAll(PsiTreeUtil.collectElementsOfType(injectedPsi, CodeAnnotationFeatureImpl.class));
                    });
                    for(PsiElement el : injectedElements){
                        if(!featureNames.contains(el.getText()))
                            featureNames.add(el.getText());
                    }
                }
            }
        }
        if(featureNames.size() != 0)
            return featureNames;
        return null;
    }

    private static List<String> extractFeaturesOfCodeBlock(List<PsiElement> elements){
        List<String> featureNames = new ArrayList<>();
        for(PsiElement element : elements){
            if (element instanceof PsiComment) {
                if (element instanceof PsiLanguageInjectionHost) {
                    PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) element;
                    InjectedLanguageManager manager = InjectedLanguageManager.getInstance(host.getProject());
                    List<PsiElement> injectedElements = new ArrayList<>();
                    manager.enumerate(host, (injectedPsi, places) -> {
                        injectedElements.addAll(PsiTreeUtil.collectElementsOfType(injectedPsi, CodeAnnotationFeatureImpl.class));
                    });
                    for(PsiElement el : injectedElements){
                        if(!featureNames.contains(el.getText()))
                            featureNames.add(el.getText());
                    }
                }
            }
        }
        if(featureNames.size() != 0)
            return featureNames;
        return null;
    }
}
