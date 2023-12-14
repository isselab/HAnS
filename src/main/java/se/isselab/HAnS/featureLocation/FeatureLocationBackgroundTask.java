package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.application.ApplicationManager;
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
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.singleton.HAnSManager;
import se.isselab.HAnS.singleton.NotifyOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Background task only for ReferencesSearch.search()
 */
public class FeatureLocationBackgroundTask extends Task.Backgroundable{

    // Removed, see HAnSManager.java
    // private List<FeatureModelFeature> featureList;



    public FeatureLocationBackgroundTask(@Nullable Project project,
                                         @NlsContexts.ProgressTitle @NotNull String title,
                                         boolean canBeCancelled,
                                         @Nullable PerformInBackgroundOption backgroundOption) {
        super(project, title, canBeCancelled, backgroundOption);
    }

    @Override
    public void run(@NotNull ProgressIndicator progressIndicator) {
        ApplicationManager.getApplication().runReadAction(() ->
        {
            HAnSManager singleton = HAnSManager.getInstance();
            List<Collection<PsiReference>> psiReferences = new ArrayList<>();
            List<FeatureModelFeature> featureList = FeatureModelUtil.findFeatures(singleton.getProject());

            for (var feature : featureList) {
                Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(super.getProject()), true);
                psiReferences.add(featureReference.findAll());
            }
            Logger.print("task done");
            singleton.setPsiReferences(psiReferences);
            singleton.notifyObservers(NotifyOption.INITIALISATION);
            Logger.print("PsiReferences set and Observers notified");
        });
    }
}
