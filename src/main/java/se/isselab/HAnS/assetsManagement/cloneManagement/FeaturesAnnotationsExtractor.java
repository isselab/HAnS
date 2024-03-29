package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationFeatureImpl;

import java.util.ArrayList;
import java.util.List;

public class FeaturesAnnotationsExtractor {

    public static void saveExtractedFeatureAnnotations(PsiElement element){
        var featuresAnnotated = extractFeatureNames(element);
        if(featuresAnnotated != null )
            FeaturesCodeAnnotations.getInstance().setFeatureNames(featuresAnnotated);
    }

    private static List<String> extractFeatureNames(PsiElement elements) {
        List<String> featureNames = new ArrayList<>();
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                for (PsiElement element : PsiTreeUtil.findChildrenOfType(elements, PsiElement.class)) {
                    if (element instanceof PsiComment) {
                        if (element instanceof PsiLanguageInjectionHost) {
                            PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) element;
                            InjectedLanguageManager manager = InjectedLanguageManager.getInstance(host.getProject());
                            List<PsiElement> injectedElements = new ArrayList<>();
                            manager.enumerate(host, (injectedPsi, places) -> {
                                injectedElements.addAll(PsiTreeUtil.collectElementsOfType(injectedPsi, CodeAnnotationFeatureImpl.class));
                            });
                            for(PsiElement el : injectedElements){
                                if(!featureNames.contains(el.getText()))
                                    featureNames.add(el.getText());
                            }
                        }
                    }
                }
            }
        });

        if(!featureNames.isEmpty())
            return featureNames;
        return null;
    }
}
