package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PlainTextTokenTypes;
import se.ch.HAnS.folderAnnotations.psi.FolderAnnotationTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FeatureAnnotationsDictionaryContributor extends CompletionContributor {
    public FeatureAnnotationsDictionaryContributor() {
        // &begin[CodeAnnotationCompletion]
        extend(CompletionType.BASIC,
                psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                new DictionaryCompletionProvider(false));

        extend(CompletionType.BASIC,
                psiElement().with(new CommentPattern()),
                new DictionaryCompletionProvider(false));
        // &end[CodeAnnotationCompletion]

        // &begin[FeatureToFolderCompletion]
        extend(CompletionType.BASIC,
                psiElement(FolderAnnotationTypes.FEATURENAME),
                new DictionaryCompletionProvider(false));
        // &end[FeatureToFolderCompletion]

        // &begin[FeatureToFileCompletion]
/*
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FileAnnotationsTypes.FEATURE_NAME),
                new DictionaryCompletionProvider(false));
 */
        /*
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FileAnnotationsTypes.FILE_NAME),
                new FileNamesCompletionProvider(false));
         */
        // &end[FeatureToFileCompletion]
    }
}
