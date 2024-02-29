package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CloneCopyPastePreProcessor implements CopyPastePreProcessor {
    @Override
    public @Nullable String preprocessOnCopy(PsiFile psiFile, int[] ints, int[] ints1, String s) {
        Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
        int startLine = document.getLineNumber(ints[0]);
        int endLine = document.getLineNumber(ints1[0]);
        boolean isMultiLineCopy = endLine > startLine;
        System.out.println(startLine +" start " + endLine + " end " + isMultiLineCopy);
        if(isMultiLineCopy){
            PsiElement startElement = psiFile.findElementAt(ints[0]);
            while (startElement != null && !(startElement instanceof PsiMethod) && !(startElement instanceof PsiClass)) {
                startElement = startElement.getParent();
            }
            Project project = psiFile.getProject();
            PsiMethod copiedMethod = null;
            PsiClass copiedClass = null;
            if (startElement instanceof PsiMethod) {
                copiedMethod = (PsiMethod) startElement;
                System.out.println(copiedMethod.getName());
                CloneManager.CloneMethodAssets(project, psiFile, copiedMethod);
            }else if(startElement instanceof PsiClass) {
                copiedClass = (PsiClass) startElement;
                System.out.println(copiedClass.getName());
                CloneManager.CloneClassAssets(project, psiFile, copiedClass);
            }
        }
        return s;
    }

    @Override
    public @NotNull String preprocessOnPaste(Project project, PsiFile psiFile, Editor editor, String s, RawText rawText) {
        if (psiFile != null) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                System.out.println("Pasting into: " + virtualFile.getPath());
            }
        }
        return s;
    }
}
