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
/*        extend(CompletionType.BASIC,
                psiElement(PsiComment.class),
                new KeywordCompletionProvider(false));*/
        // &end[CodeAnnotationCompletion]


        // &begin[FeatureToFileCompletion]
        extend(CompletionType.BASIC,
                psiElement(FileAnnotationTypes.STRING).
                        andNot(psiElement(FileAnnotationTypes.STRING).
                                withParent(FileAnnotationFeatureNameImpl.class)),
                new FileNameCompletionProvider(false));
        // &end[FeatureToFileCompletion]
    }
}
