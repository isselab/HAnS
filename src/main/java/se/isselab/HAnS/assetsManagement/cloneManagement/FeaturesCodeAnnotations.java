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
