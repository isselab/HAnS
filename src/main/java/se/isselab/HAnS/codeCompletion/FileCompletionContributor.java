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
package se.isselab.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationTypes;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFeatureNameImpl;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FileCompletionContributor extends CompletionContributor {
    public FileCompletionContributor() {
        // &begin[FileNameProvider]
        extend(CompletionType.BASIC,
                psiElement(FileAnnotationTypes.STRING).
                        andNot(psiElement(FileAnnotationTypes.STRING).
                                withParent(FileAnnotationFeatureNameImpl.class)),
                new FileNameCompletionProvider(false));
        // &end[FileNameProvider]
    }
}
