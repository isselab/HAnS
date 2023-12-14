package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


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
        /*
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
        */

    }
}
