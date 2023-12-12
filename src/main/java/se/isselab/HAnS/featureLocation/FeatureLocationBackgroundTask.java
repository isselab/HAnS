package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.FeatureAnnotationSearchScope;
import se.isselab.HAnS.Logger;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Collection;
import java.util.List;

public class FeatureLocationBackgroundTask extends Task.Backgroundable{
    private Collection<PsiReference> psiReferences;
    private List<FeatureModelFeature> featureList;



    public FeatureLocationBackgroundTask(@Nullable Project project,
                                         @NlsContexts.ProgressTitle @NotNull String title,
                                         boolean canBeCancelled,
                                         @Nullable PerformInBackgroundOption backgroundOption,
                                         List<FeatureModelFeature> featureList) {
        super(project, title, canBeCancelled, backgroundOption);
        this.featureList = featureList;
    }

    public Collection<PsiReference> getPsiReference (){
        return psiReferences;
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        for(var feature : featureList) {
            Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(super.getProject()), true);
            psiReferences = featureReference.findAll();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Logger.print("task done");
    }
}
