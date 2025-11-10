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
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.CodeAnnotationLpq;
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.impl.CodeAnnotationPsiImplUtil;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureAnnotation.fileAnnotation.psi.FileAnnotationLpq;
import se.isselab.HAnS.featureAnnotation.fileAnnotation.psi.impl.FileAnnotationPsiImplUtil;
import se.isselab.HAnS.featureAnnotation.folderAnnotation.psi.FolderAnnotationLpq;
import se.isselab.HAnS.featureAnnotation.folderAnnotation.psi.impl.FolderAnnotationPsiImplUtil;

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
        } else {
            newLPQ = newElementName;
        }

        switch (myElement) {
            case FolderAnnotationLpq folderAnnotationLpq ->
                    FolderAnnotationPsiImplUtil.setName(folderAnnotationLpq, newLPQ);
            case FileAnnotationLpq fileAnnotationLpq -> FileAnnotationPsiImplUtil.setName(fileAnnotationLpq, newLPQ);
            case CodeAnnotationLpq codeAnnotationLpq -> CodeAnnotationPsiImplUtil.setName(codeAnnotationLpq, newLPQ);
            default -> {
                return myElement;
            }
        }
        return myElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        Project project = myElement.getProject();
        final List<FeatureModelFeature> features = FeatureModelUtil.findFullLPQ(project, lpq);
        List<ResolveResult> results = new ArrayList<>();
        for (FeatureModelFeature feature : features) {
            results.add(new PsiElementResolveResult(feature));
        }
        return results.size() == 1 ? results.getFirst().getElement() : null;
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
}