package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.codeInsight.editorActions.CopyPastePreProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.RawText;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CloneCopyPastePreProcessor implements CopyPastePreProcessor {
    PsiClass copiedClass;
    PsiMethod copiedMethod;
    String sourcePath;
    String sourceProjectName;
    @Override
    public @Nullable String preprocessOnCopy(PsiFile psiFile, int[] ints, int[] ints1, String s) {
        resetClones();
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
            sourcePath = psiFile.getVirtualFile().getPath();
            sourceProjectName = psiFile.getProject().getName();
            if (startElement instanceof PsiMethod) {
                copiedMethod = (PsiMethod) startElement;
            }else if(startElement instanceof PsiClass) {
                copiedClass = (PsiClass) startElement;
            }
        }
        return s;
    }

    @Override
    public @NotNull String preprocessOnPaste(Project project, PsiFile psiFile, Editor editor, String s, RawText rawText) {
        String currentMethodName = "";
        String currentClassName = "";
        if (psiFile != null && editor != null) {
            int caretOffset = editor.getCaretModel().getOffset();
            PsiElement elementAtCaret = psiFile.findElementAt(caretOffset);
            PsiMethod currentMethod = null;
            while (elementAtCaret != null) {
                if (elementAtCaret instanceof PsiMethod) {
                    currentMethod = (PsiMethod) elementAtCaret;
                    break;
                }
                elementAtCaret = elementAtCaret.getParent();
            }
            if(currentMethod != null){
                currentMethodName = currentMethod.getName();
            }
            PsiClass currentClass = null;
            while (elementAtCaret != null) {
                if (elementAtCaret instanceof PsiClass) {
                    currentClass = (PsiClass) elementAtCaret;
                    break;
                }
                elementAtCaret = elementAtCaret.getParent();
            }
            if(currentClass != null){
                currentClassName = currentClass.getName();
            }
        }
        System.out.println("method" + currentMethodName);
        System.out.println("class" + currentClassName);

        if (copiedClass != null) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                String targetPath = virtualFile.getPath();
                CloneManager.CloneClassAssets(project, sourceProjectName, sourcePath, targetPath, copiedClass, currentClassName);
            }
        } else if(copiedMethod != null){
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile != null) {
                String targetPath = virtualFile.getPath();
                PsiClass parentClass = PsiTreeUtil.getParentOfType(copiedMethod, PsiClass.class);
                CloneManager.CloneMethodAssets(project,sourceProjectName, sourcePath, targetPath, parentClass.getName(), copiedMethod, currentClassName, currentMethodName );
            }
        }
        return s;
    }
    private void resetClones(){
        copiedMethod = null;
        copiedClass = null;
        sourcePath = null;
        sourceProjectName = null;
    }
}
