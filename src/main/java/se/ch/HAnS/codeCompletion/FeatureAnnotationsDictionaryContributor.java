package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;
import se.ch.HAnS.folderAnnotations.psi.FolderAnnotationTypes;

import java.lang.annotation.ElementType;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FeatureAnnotationsDictionaryContributor extends CompletionContributor {
    public FeatureAnnotationsDictionaryContributor() {
        extend(CompletionType.BASIC,
                psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                new DictionaryCompletionProvider(false));

        extend(CompletionType.BASIC,
                psiElement(FolderAnnotationTypes.FEATURENAME),
                new DictionaryCompletionProvider(false));
/*
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FileAnnotationsTypes.FEATURE_NAME),
                new DictionaryCompletionProvider(false));
 */
        extend(CompletionType.BASIC,
                psiElement().with(new CommentPattern()),
                new DictionaryCompletionProvider(false));
        /*
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FileAnnotationsTypes.FILE_NAME),
                new FileNamesCompletionProvider(false));
         */
    }
}
