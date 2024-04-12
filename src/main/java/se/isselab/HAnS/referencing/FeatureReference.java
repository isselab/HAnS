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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.AnnotationIcons;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationPsiImplUtil;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationPsiImplUtil;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationLpq;
import se.isselab.HAnS.folderAnnotation.psi.impl.FolderAnnotationPsiImplUtil;

import java.util.*;

public class FeatureReference extends PsiReferenceBase<PsiElement> {

    private final String lpq;

    public FeatureReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
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

    @Nullable
    @Override
    public PsiElement resolve() {
        Project project = myElement.getProject();
        final List<FeatureModelFeature> features = FeatureModelUtil.findLPQ(project, lpq);
        List<ResolveResult> results = new ArrayList<>();
        for (FeatureModelFeature feature : features) {
            results.add(new PsiElementResolveResult(feature));
        }
        return results.size() == 1 ? results.get(0).getElement() : null;
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        Project project = myElement.getProject();
        List<FeatureModelFeature> features = FeatureModelUtil.findFeatures(project);
        List<LookupElement> variants = new ArrayList<>();
        for (final FeatureModelFeature feature : features) {
            if (feature.getText() != null && feature.getText().length() > 0) {
                variants.add(LookupElementBuilder
                        .create(feature.getLPQText()).withIcon(AnnotationIcons.FileType)
                        .withTypeText(feature.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

}