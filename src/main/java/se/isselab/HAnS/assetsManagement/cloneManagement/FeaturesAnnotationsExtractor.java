/*
Copyright 2024 Ahmad Al Shihabi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationFeatureImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FeaturesAnnotationsExtractor {

    public static void saveExtractedFeatureAnnotations(PsiElement element){
        var featuresAnnotated = extractFeatureNames(element);
        if(featuresAnnotated != null )
            FeaturesCodeAnnotations.getInstance().setFeatureNames(featuresAnnotated);
    }

    public static List<String> extractFeatureNames(PsiElement elements) {
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
        return featureNames.isEmpty() ? null : featureNames;
    }

    public static List<String> getFeaturesAnnotationsFromText(String copiedText){
        List<String> features = new ArrayList<>();
        String regex = "// &(line|begin|end)\\[([^\\]]*)\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(copiedText);
        while (matcher.find()) {
            String[] featureParts = matcher.group(2).split(",\\s*");
            for (String part : featureParts) {
                String[] words = part.split("::");
                for (String word : words) {
                    if (!word.isEmpty() && !features.contains(word.trim())) {
                        features.add(word.trim());
                    }
                }
            }
        }
        return features.isEmpty() ? null : features;
    }
}
