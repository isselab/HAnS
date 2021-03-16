package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PlainTextTokenTypes;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;
import se.ch.HAnS.fileAnnotations.psi.impl.FileAnnotationsFeatureNameImpl;
import se.ch.HAnS.fileAnnotations.psi.impl.FileAnnotationsFileReferenceImpl;
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
                new KeywordCompletionProvider(false));
        // &end[CodeAnnotationCompletion]

        // &begin[FeatureToFolderCompletion]
        extend(CompletionType.BASIC,
                psiElement(FolderAnnotationTypes.FEATURENAME),
                new DictionaryCompletionProvider(false));
        // &end[FeatureToFolderCompletion]

        // &begin[FeatureToFileCompletion]

        extend(CompletionType.BASIC,
                psiElement(FileAnnotationsTypes.STRING),
                new DictionaryCompletionProvider(false));

        extend(CompletionType.BASIC,
                psiElement(FileAnnotationsTypes.FILE_NAME).withParent(FileAnnotationsFileReferenceImpl.class),
                new FileNamesCompletionProvider(false));
        // &end[FeatureToFileCompletion]
    }
}
