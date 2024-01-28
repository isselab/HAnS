package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.codeInsight.template.impl.actions.SurroundWithTemplateAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CloneAssetCode extends AnAction {
    public static List<PsiElement> elementsInRange;
    public static PsiMethod clonedMethod;
    public static PsiClass clonedClass;
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile psiFile = PsiDocumentManager.getInstance(anActionEvent.getProject()).getPsiFile(editor.getDocument());
        if (psiFile == null) return;
        SelectionModel selectionModel = editor.getSelectionModel();
        resetClones();
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
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
        tracingHandler.createCodeAssetsTrace();
    }

    private void resetClones() {
        clonedClass = null;
        clonedMethod = null;
        elementsInRange = null;
    }

    private void cloneClass(PsiElement element) {
        PsiClass classAtCaret = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        clonedClass = classAtCaret;
    }

    private void cloneMethod(PsiMethod methodAtCaret) {
        clonedMethod = methodAtCaret;
    }

    private void getHighlightedBlock(SelectionModel selectionModel, PsiFile psiFile){
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
        elementsInRange = elements;
    }

}


