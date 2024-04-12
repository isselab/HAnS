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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FileReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String file;

    public FileReference(@NotNull PsiElement element, TextRange textRange) {
        super(element, textRange);
        file = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    }

    @NotNull
    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        final List<PsiFile> files = FileReferenceUtil.findFile(myElement, file);
        List<ResolveResult> results = new ArrayList<>();
        for (PsiFile file : files) {
            results.add(new PsiElementResolveResult(file));
        }
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object @NotNull [] getVariants() {
        List<PsiFile> files = FileReferenceUtil.findFiles(myElement);
        List<LookupElement> variants = new ArrayList<>();
        for (final PsiFile file : files) {
            if (file.getName().length() > 0) {
                variants.add(LookupElementBuilder
                        .create(file.getName())
                );
            }
        }
        return variants.toArray();
    }

}