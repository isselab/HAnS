package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.codeInsight.template.impl.actions.SurroundWithTemplateAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CloneAssetCode extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
// Get the current editor
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        // Get the document and project
        PsiFile psiFile = PsiDocumentManager.getInstance(anActionEvent.getProject()).getPsiFile(editor.getDocument());
        if (psiFile == null) return;

        // Get the selection model
        SelectionModel selectionModel = editor.getSelectionModel();

        // Check if something is selected
        if (selectionModel.hasSelection()) {
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
            Iterator iterator = elementsInRange.iterator();
            while(iterator.hasNext()){
                System.out.println("Element is " + ((PsiElement) iterator.next()).getText());
            }

        }
    }
}
