/*
Copyright 2024 Ahmad Al Shihabi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.assetsManagement.cloneManagement;

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

    @Override
    public @Nullable String preprocessOnCopy(PsiFile psiFile, int[] ints, int[] ints1, String s) {
        if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isClonePreference() || AssetsAndFeatureTraces.isCloneAndPropagatePreference() || AssetsAndFeatureTraces.isCloneAndShowClonePreference()) {
            AssetsAndFeatureTraces.resetAssetClones();
            Document document = PsiDocumentManager.getInstance(psiFile.getProject()).getDocument(psiFile);
            int startLine = document.getLineNumber(ints[0]);
            int endLine = document.getLineNumber(ints1[0]);
            boolean isMultiLineCopy = endLine > startLine;
            if(isMultiLineCopy){
                PsiElement startElement = psiFile.findElementAt(ints[0]);
                while (startElement != null && !(startElement instanceof PsiMethod) && !(startElement instanceof PsiClass)) {
                    startElement = startElement.getParent();
                }
                AssetsAndFeatureTraces.sourcePath = psiFile.getVirtualFile().getPath();
                AssetsAndFeatureTraces.sourceProjectName = psiFile.getProject().getName();
                if (startElement instanceof PsiMethod) {
                    AssetsAndFeatureTraces.clonedMethod = (PsiMethod) startElement;
                }else if(startElement instanceof PsiClass) {
                    AssetsAndFeatureTraces.clonedClass = (PsiClass) startElement;
                }
            }
        }

        return s;
    }

    @Override
    public @NotNull String preprocessOnPaste(Project project, PsiFile psiFile, Editor editor, String s, RawText rawText) {
        if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isClonePreference() || AssetsAndFeatureTraces.isCloneAndPropagatePreference() || AssetsAndFeatureTraces.isCloneAndShowClonePreference()){
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
            var featuresAnnotated = FeatureModelHandler.getFeaturesAnnotationsFromText(s);
            if(featuresAnnotated != null )
                FeaturesCodeAnnotations.getInstance().setFeatureNames(featuresAnnotated);
            if (AssetsAndFeatureTraces.clonedClass != null) {
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null) {
                    String targetPath = virtualFile.getPath();
                    CloneManager.CloneClassAssets(project, AssetsAndFeatureTraces.sourceProjectName, AssetsAndFeatureTraces.sourcePath, targetPath, AssetsAndFeatureTraces.clonedClass, currentClassName);
                }
            } else if(AssetsAndFeatureTraces.clonedMethod != null){
                VirtualFile virtualFile = psiFile.getVirtualFile();
                if (virtualFile != null) {
                    String targetPath = virtualFile.getPath();
                    PsiClass parentClass = PsiTreeUtil.getParentOfType(AssetsAndFeatureTraces.clonedMethod, PsiClass.class);
                    CloneManager.CloneMethodAssets(project, AssetsAndFeatureTraces.sourceProjectName, AssetsAndFeatureTraces.sourcePath, targetPath, parentClass.getName(), AssetsAndFeatureTraces.clonedMethod, currentClassName, currentMethodName );
                }
            }
        }
        return s;
    }
}
