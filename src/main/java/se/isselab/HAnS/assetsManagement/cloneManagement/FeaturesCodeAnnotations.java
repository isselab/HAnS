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

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

public class FeaturesCodeAnnotations {
    private final static FeaturesCodeAnnotations instance = new FeaturesCodeAnnotations();
    private List<String> featureNames;

    private FeaturesCodeAnnotations() {
        featureNames = new ArrayList<>();
    }

    public static FeaturesCodeAnnotations getInstance() {
        return instance;
    }

    public synchronized List<String> getFeatureNames() {
        return new ArrayList<>(featureNames);
    }

    public synchronized void setFeatureNames(List<String> newFeatures) {
        this.featureNames = newFeatures;
    }
    public synchronized void addFeatures(List<PsiElement> newElements){
        for(PsiElement element : newElements){
            if(!this.featureNames.contains(getText(element))){
                this.featureNames.add(getText(element));
            }
        }
    }
    private String getText(PsiElement element) {
        final String[] textHolder = new String[1];
        ApplicationManager.getApplication().runReadAction(new Runnable() {
            @Override
            public void run() {
                textHolder[0] = element.getText();
            }
        });
        return textHolder[0];
    }
    public synchronized void clearFeatures(){
        this.featureNames.clear();
    }
}
