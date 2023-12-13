package se.isselab.HAnS.singleton;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiReference;
import se.isselab.HAnS.featureExtension.HAnSObserverInterface;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Manages every incoming calls and delegates them to subclasses that may be protected, e.g. {@link FeatureSubject}.
 * Does the routing from other plugins. Plugins will receive this Singleton object by retreiving the service {@link se.isselab.HAnS.TestService}
 * @see FeatureSubject
 * @see se.isselab.HAnS.TestService
 */
public class HAnSManager {
    private static final HAnSManager HANSMANAGER = new HAnSManager();

    // TODO: Where should we put the featureList?
    private List<FeatureModelFeature> featureList;

    private HashMap<FeatureModelFeature, FeatureFileMapping> featureMapping;
    private final FeatureLocationManager featureLocationManager;
    /**
     * Observable of {@link HAnSObserverInterface}
     */
    private final FeatureSubject featureSubject;


    // TODO: Check correctness
    private final Project project = ProjectManager.getInstance().getOpenProjects()[0];


    private HAnSManager(){

        // TODO: Where should we put the featureList?
        // -> we don't need it. We'll use FeatureModelUtil.findFeatures(project);
        // featureList = FeatureModelUtil.findFeatures(project);

        featureLocationManager = FeatureLocationManager.getFeatureLocationManager();
        featureSubject = FeatureSubject.getFeatureSubject();
        registerObserver(featureLocationManager, NotifyOption.INITIALISATION);
    }

    public static HAnSManager getInstance() {
        return HANSMANAGER;
    }




    /*public List<FeatureModelFeature> getFeatureList() {
        // TODO: Check this "Read access is allowed from inside read-action (or EDT) only"
        return FeatureModelUtil.findFeatures(project);
    }*/

    public Project getProject() {
        return project;
    }

    public List<Collection<PsiReference>> getPsiReferences() {
        // TODO: Proxy to hide functionality from other plugins
        return featureLocationManager.getPsiReferences();
    }

    public void setPsiReferences(List<Collection<PsiReference>> psiReferences) {
        featureLocationManager.setPsiReferences(psiReferences);
    }

    public HashMap<FeatureModelFeature, FeatureFileMapping> getFeatureMapping() {
        HashMap<FeatureModelFeature,FeatureFileMapping> copy = featureMapping;
        return copy;
    }

    public void setFeatureMapping(HashMap<FeatureModelFeature, FeatureFileMapping> featureMapping) {
        this.featureMapping = featureMapping;
    }

    public void registerObserver(HAnSObserverInterface observer, NotifyOption option) {
        featureSubject.registerObserver(observer, option);
    }
    public void notifyObservers(NotifyOption option) {
        featureSubject.notifyObservers(option);
    }
}
