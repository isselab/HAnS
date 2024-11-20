/*
Copyright 2021 Herman Jansson & Johan Martinson

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
package se.isselab.HAnS.referencing;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationPsiImplUtil;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationPsiImplUtil;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationLpq;
import se.isselab.HAnS.folderAnnotation.psi.impl.FolderAnnotationPsiImplUtil;

import java.util.*;

public class FeatureReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private final String lpq;
    private PsiElement element;

    public FeatureReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        this.element = element;
        lpq = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        String newLPQ;
        if ((myElement.getReferences()[0].resolve() == FeatureReferenceUtil.getOrigin()
                || FeatureReferenceUtil.getOrigin() == null)
                && !FeatureReferenceUtil.getAddingOrDeleting()) {
            newLPQ = FeatureReferenceUtil.getLPQ((FeatureModelFeature) myElement.getReferences()[0].resolve(), newElementName);
        }
        else {
            newLPQ = newElementName;
        }

        if (myElement instanceof FolderAnnotationLpq) {
            FolderAnnotationPsiImplUtil.setName((FolderAnnotationLpq) myElement, newLPQ);
        }
        else if (myElement instanceof FileAnnotationLpq) {
            FileAnnotationPsiImplUtil.setName((FileAnnotationLpq) myElement, newLPQ);
        }
        else if (myElement instanceof CodeAnnotationLpq) {
            CodeAnnotationPsiImplUtil.setName((CodeAnnotationLpq) myElement, newLPQ);
        }
        return myElement;
    }


    @Override
    public Object @NotNull [] getVariants() {
        Project project = myElement.getProject();
        List<FeatureModelFeature> features = FeatureModelUtil.findFeatures(project);
        List<LookupElement> variants = new ArrayList<>();
        for (final FeatureModelFeature feature : features) {
            if (feature.getText() != null && !feature.getText().isEmpty()) {
                variants.add(LookupElementBuilder
                        .create(feature.getLPQText()).withIcon(AnnotationIcons.FileType)
                        .withTypeText(feature.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

    private boolean isEndTag(PsiElement psiElement) {
        if (psiElement.getText().startsWith("&end")) return true;
        if (psiElement.getParent() != null) {
            return isEndTag(psiElement.getParent());
        }
        return false;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean b) {
        Project project = element.getProject();

        List<ResolveResult> results = new ArrayList<>();

        if(element instanceof FileAnnotationLpq) {
            if(element.getParent() != null) {
                String[] lines = element.getParent().getContainingFile().getText().split("\n");
                String fileName = lines[0];

                VirtualFile currentFile = element.getParent().getContainingFile().getVirtualFile();
                VirtualFile parentFolder = currentFile != null ? currentFile.getParent() : null;

                VirtualFile targetFile = null;

                if (parentFolder != null) {
                    for (VirtualFile file : parentFolder.getChildren()) {
                        if (!file.isDirectory() && file.getName().equals(fileName)) {
                            targetFile = file;
                            break;
                        }
                    }
                }

                if(targetFile == null) return new ResolveResult[0];

                PsiFile fileAsPsi = PsiManager.getInstance(project).findFile(targetFile);

                if(fileAsPsi == null) return new ResolveResult[0];

                results.add(new PsiElementResolveResult(fileAsPsi));
            }
        } else if(element instanceof CodeAnnotationLpq) {
            if (isEndTag(element)) return ResolveResult.EMPTY_ARRAY;
            final List<FeatureModelFeature> features = FeatureModelUtil.findLPQ(project, lpq);

            for (FeatureModelFeature feature : features) {

                PsiElement commentElement = ReadAction.compute(() -> PsiTreeUtil.getContextOfType(element, PsiComment.class));

                if(commentElement == null) continue;

                PsiFile file = commentElement.getContainingFile();
                String[] lines = file.getText().split("\n");

                int beginLineNumber = getLine(project, commentElement);
                int endLineNumber = 1;

                for(String line : lines) {
                    if(endLineNumber > beginLineNumber && line.contains("&end[" + feature.getName() + "]")){
                        break;
                    }
                    endLineNumber++;
                }


                // Add the FeatureModelFeature itself as a result
                results.add(new PsiElementResolveResult(feature));
            }
        }
        return results.toArray(new ResolveResult[0]);
    }

    private int getLine(Project project, PsiElement elem) {

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile openedFile = ReadAction.compute(elem::getContainingFile);

        //iterate over each psiElement and check for PsiComment-Feature-Annotations
        if (openedFile == null)
            return -1;
        Document document = psiDocumentManager.getDocument(openedFile);
        if (document == null)
            return -1;

        return document.getLineNumber(elem.getTextRange().getStartOffset());
    }
}