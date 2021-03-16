package se.ch.HAnS.codeAnnotations;

import com.intellij.lang.Commenter;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

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
                String lineCommentPrefix = commenter.getLineCommentPrefix();
                String blockCommentPrefix = commenter.getBlockCommentPrefix();
                String blockCommentSuffix = commenter.getBlockCommentSuffix();

                registrar.startInjecting(CodeAnnotationLanguage.INSTANCE);
                // &begin[Injection::JavaStyleComment]
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
                // &end[Injection::JavaStyleComment]
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