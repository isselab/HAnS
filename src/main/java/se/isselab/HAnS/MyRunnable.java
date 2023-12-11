package se.isselab.HAnS;

import com.github.rjeschke.txtmark.Run;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyRunnable implements Runnable {

    private Collection<PsiReference> psiReferences;
    private List<FeatureModelFeature> featureList;
    private Project project;
    // private final Query<PsiReference> query;
    public MyRunnable(List<FeatureModelFeature> list, Project project) {
        featureList = list;
        this.project = project;
    }
    @Override
    public void run() {
        for(var feature : featureList) {
            Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(project), true);
            psiReferences = featureReference.findAll();
        }
    }
    public Collection<PsiReference> getPsiReference (){
        return psiReferences;
    }

}
