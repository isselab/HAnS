package se.ch.HAnS.featureModel;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;

public class FeatureModelReferenceContributor extends PsiReferenceContributor {

    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        System.out.println("At least recognised");
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(FolderAnnotationLpq.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                           @NotNull ProcessingContext context) {
                        System.out.println("Success in a way");
                        return new PsiReference[]{new FeatureModelReference(element, element.getTextRange())};
                    }
                });
    }

}