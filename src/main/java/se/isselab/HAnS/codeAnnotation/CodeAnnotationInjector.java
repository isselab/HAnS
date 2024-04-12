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
package se.isselab.HAnS.codeAnnotation;

import com.intellij.lang.Commenter;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

class CodeAnnotationInjector implements MultiHostInjector {
    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (context instanceof PsiDocCommentBase) {
            return;
        }

        if (context instanceof PsiComment) {
            String pattern = ".*(&begin|&line|&end).*";
            if (Pattern.matches(pattern, context.getText())) {
                final PsiComment psiComment = (PsiComment) context;

                Commenter commenter = LanguageCommenters.INSTANCE.forLanguage(psiComment.getLanguage());

                if (commenter == null)
                    commenter = new Commenter() {
                        @Override
                        public @Nullable String getLineCommentPrefix() {
                            return "//";
                        }

                        @Override
                        public @Nullable String getBlockCommentPrefix() {
                            return "/*";
                        }

                        @Override
                        public @Nullable String getBlockCommentSuffix() {
                            return "*/";
                        }

                        @Override
                        public @Nullable String getCommentedBlockCommentPrefix() {
                            return null;
                        }

                        @Override
                        public @Nullable String getCommentedBlockCommentSuffix() {
                            return null;
                        }
                    };

                String lineCommentPrefix = commenter.getLineCommentPrefix();
                String blockCommentPrefix = commenter.getBlockCommentPrefix();
                String blockCommentSuffix = commenter.getBlockCommentSuffix();

                registrar.startInjecting(CodeAnnotationLanguage.INSTANCE);
                // &begin[JavaStyleComment]

                if (psiComment.getTokenType().toString().equals("END_OF_LINE_COMMENT") && lineCommentPrefix != null) {
                    registrar.addPlace(
                            null,
                            null,
                            (PsiLanguageInjectionHost) context,
                            TextRange.create(lineCommentPrefix.length(), psiComment.getTextLength()));

                } else if (psiComment.getTokenType().toString().equals("C_STYLE_COMMENT") && blockCommentPrefix != null && blockCommentSuffix != null) {
                    registrar.addPlace(
                            null,
                            null,
                            (PsiLanguageInjectionHost) context,
                            TextRange.create(blockCommentPrefix.length(), psiComment.getTextLength() - blockCommentSuffix.length()));
                // &end[JavaStyleComment]
                } else {
                    registrar.addPlace(
                            null,
                            null,
                            (PsiLanguageInjectionHost) context,
                            TextRange.allOf(psiComment.getText()));
                }

                registrar.doneInjecting();
            }
        }
    }

    @Override
    public @NotNull List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(PsiComment.class);
    }
}