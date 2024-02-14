package se.isselab.HAnS.vpIntegration;

import com.intellij.psi.PsiElement;

import java.util.ArrayList;
import java.util.List;

public class FeaturesCodeAnnotations {
    private static FeaturesCodeAnnotations instance = new FeaturesCodeAnnotations();
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
            if(!this.featureNames.contains(element.getText())){
                this.featureNames.add(element.getText());
            }
        }
    }
    public synchronized void clearFeatures(){
        this.featureNames.clear();
    }
}
