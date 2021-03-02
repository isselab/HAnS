package se.ch.HAnS.autoCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PlainTextTokenTypes;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;
import se.ch.HAnS.folderAnnotations.psi.FolderAnnotationTypes;

public class FeatureNameDictionaryContributor extends CompletionContributor {
    public FeatureNameDictionaryContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PlainTextTokenTypes.PLAIN_TEXT),
                new DictionaryCompletionProvider(false));

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FolderAnnotationTypes.FEATURENAME),
                new DictionaryCompletionProvider(false));

        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(FileAnnotationsTypes.FEATURE_NAME),
                new DictionaryCompletionProvider(false));
    }
}
