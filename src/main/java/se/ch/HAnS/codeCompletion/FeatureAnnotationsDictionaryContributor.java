package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PlainTextTokenTypes;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import se.ch.HAnS.codeAnnotations.psi.CodeAnnotationTypes;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;
import se.ch.HAnS.fileAnnotations.psi.impl.FileAnnotationsFeatureNameImpl;
import se.ch.HAnS.folderAnnotations.psi.FolderAnnotationTypes;

import static com.intellij.patterns.PlatformPatterns.psiElement;

public class FeatureAnnotationsDictionaryContributor extends CompletionContributor {
    public FeatureAnnotationsDictionaryContributor() {
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
                psiElement(FileAnnotationsTypes.STRING).
                        andNot(psiElement(FileAnnotationsTypes.STRING).
                                withParent(FileAnnotationsFeatureNameImpl.class)),
                new FileNameCompletionProvider(false));

        extend(CompletionType.BASIC,
                psiElement(FileAnnotationsTypes.STRING).withParent(FileAnnotationsFeatureNameImpl.class),
                new FeatureNameCompletionProvider(false));
        // &end[FeatureToFileCompletion]
    }
}
