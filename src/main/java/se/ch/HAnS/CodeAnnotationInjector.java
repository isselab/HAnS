package se.ch.HAnS;

import com.intellij.lang.Commenter;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageCommenters;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.codeAnnotations.CodeAnnotationsLanguage;
import se.ch.HAnS.folderAnnotations.FolderAnnotationLanguage;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

class CodeAnnotationInjector implements MultiHostInjector {

    // &begin[feature]
    /* &line[feature] */

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
        if (context instanceof PsiDocCommentBase) {
            return;
        }

        //List<String> lineCommentPrefixes = Commenter.getLineCommentPrefixes();
        /*@NotNull List<Commenter> allCommentersForLanguage = LanguageCommenters.INSTANCE.allForLanguageOrAny(Language.ANY);*/

        if (context instanceof PsiComment) {
            String pattern = ".*(&begin|&line|&end).*";
            if (Pattern.matches(pattern, context.getText())) {
                final PsiComment psiComment = (PsiComment) context;

                Commenter commenter = LanguageCommenters.INSTANCE.forLanguage(psiComment.getLanguage());
                String lineCommentPrefix = commenter.getLineCommentPrefix();
                String blockCommentPrefix = commenter.getBlockCommentPrefix();
                String blockCommentSuffix = commenter.getBlockCommentSuffix();

                registrar.startInjecting(CodeAnnotationsLanguage.INSTANCE);
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