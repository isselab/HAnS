package se.ch.HAnS.featureModel;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.codeAnnotation.psi.CodeAnnotationLpq;
import se.ch.HAnS.codeAnnotation.psi.impl.CodeAnnotationLpqImpl;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationLpq;
import se.ch.HAnS.fileAnnotation.psi.impl.FileAnnotationLpqImpl;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;
import se.ch.HAnS.folderAnnotation.psi.impl.FolderAnnotationLpqImpl;

public class FeatureModelReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(FolderAnnotationLpq.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        return new PsiReference[]{new FeatureModelReference(element, element.getTextRange().shiftLeft(element.getTextOffset()))};
                    }
                });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(FileAnnotationLpq.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        return new PsiReference[]{new FeatureModelReference(element, element.getTextRange().shiftLeft(element.getTextOffset()))};
                    }
                });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(CodeAnnotationLpq.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        return new PsiReference[]{new FeatureModelReference(element, element.getTextRange().shiftLeft(element.getTextOffset()))};
                    }
                });
    }

}