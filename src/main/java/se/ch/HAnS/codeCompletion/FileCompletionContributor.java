package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationTypes;
import se.ch.HAnS.fileAnnotation.psi.impl.FileAnnotationFeatureNameImpl;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FileCompletionContributor extends CompletionContributor {
    public FileCompletionContributor() {
        // &begin[FeatureToFileCompletion]
        extend(CompletionType.BASIC,
                psiElement(FileAnnotationTypes.STRING).
                        andNot(psiElement(FileAnnotationTypes.STRING).
                                withParent(FileAnnotationFeatureNameImpl.class)),
                new FileNameCompletionProvider(false));
        // &end[FeatureToFileCompletion]
    }
}
