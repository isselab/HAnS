package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiComment;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationTypes;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationTypes;
import se.ch.HAnS.fileAnnotation.psi.impl.FileAnnotationFeatureNameImpl;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FeatureAnnotationDictionaryContributor extends CompletionContributor {
    public FeatureAnnotationDictionaryContributor() {
        // &begin[CodeAnnotationCompletion]
        extend(CompletionType.BASIC,
                psiElement(CodeAnnotationTypes.FEATURENAME),
                new FeatureNameCompletionProvider(false));

        extend(CompletionType.BASIC,
                psiElement(PsiComment.class),
                new KeywordCompletionProvider());
        // &end[CodeAnnotationCompletion]

        // &begin[FeatureToFolderCompletion]
        extend(CompletionType.BASIC,
                psiElement(FolderAnnotationTypes.FEATURENAME),
                new FeatureNameCompletionProvider(false));
        // &end[FeatureToFolderCompletion]

        // &begin[FeatureToFileCompletion]
        extend(CompletionType.BASIC,
                psiElement(FileAnnotationTypes.STRING).
                        andNot(psiElement(FileAnnotationTypes.STRING).
                                withParent(FileAnnotationFeatureNameImpl.class)),
                new FileNameCompletionProvider(false));

        extend(CompletionType.BASIC,
                psiElement(FileAnnotationTypes.STRING).withParent(FileAnnotationFeatureNameImpl.class),
                new FeatureNameCompletionProvider(false));
        // &end[FeatureToFileCompletion]
    }
}
