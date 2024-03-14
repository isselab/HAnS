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
import se.isselab.HAnS.assetsManagement.AssetsManagementSettings;

import java.util.List;

public class CloneCopyPastePreProcessor implements CopyPastePreProcessor {
    PsiClass copiedClass;
    PsiMethod copiedMethod;
    String sourcePath;
    String sourceProjectName;
    @Override
    public @Nullable String preprocessOnCopy(PsiFile psiFile, int[] ints, int[] ints1, String s) {
        if(AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("clone")
          || AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("both")) {
            resetClones();
            Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
            int startLine = document.getLineNumber(ints[0]);
            int endLine = document.getLineNumber(ints1[0]);
            boolean isMultiLineCopy = endLine > startLine;
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
        }

        return s;
    }

    @Override
    public @NotNull String preprocessOnPaste(Project project, PsiFile psiFile, Editor editor, String s, RawText rawText) {
        if(AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("clone")
          || AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("both")){
            String currentMethodName = "";
            String currentClassName = "";
            if (psiFile != null && editor != null) {
                int caretOffset = editor.getCaretModel().getOffset();
                PsiElement methodAtCaret = psiFile.findElementAt(caretOffset);
                PsiElement classAtCaret = psiFile.findElementAt(caretOffset);
                PsiMethod currentMethod = null;
                while (methodAtCaret != null) {
                    if (methodAtCaret instanceof PsiMethod) {
                        currentMethod = (PsiMethod) methodAtCaret;
                        break;
                    }
                    methodAtCaret = methodAtCaret.getParent();
                }
                if(currentMethod != null){
                    currentMethodName = currentMethod.getName();
                }
                PsiClass currentClass = null;
                while (classAtCaret != null) {
                    if (classAtCaret instanceof PsiClass) {
                        currentClass = (PsiClass) classAtCaret;
                        break;
                    }
                    classAtCaret = classAtCaret.getParent();
                }
                if(currentClass != null){
                    currentClassName = currentClass.getName();
                }
            }
            var featuresAnnotated = FeaturesHandler.getFeaturesAnnotationsFromText(s);
            if(featuresAnnotated != null )
                FeaturesCodeAnnotations.getInstance().setFeatureNames(featuresAnnotated);
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
