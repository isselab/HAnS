package se.isselab.HAnS.singleton;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureExtension.HAnSObserverInterface;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class HAnSSingleton {
    private static final HAnSSingleton hAnSSingleton = new HAnSSingleton();
    private List<HAnSObserverInterface> onUpdateObservers;
    private List<HAnSObserverInterface> onDeleteObservers;
    private List<HAnSObserverInterface> onAddObservers;
    private List<HAnSObserverInterface> onInitObservers;
    private List<FeatureModelFeature> featureList;
    private List<Collection<PsiReference>> psiReferences;
    private HashMap<FeatureModelFeature, FeatureFileMapping> featureMapping;
    private final FeatureLocationManager featureLocationManager;


    // TODO: Check correctness
    private final Project project = ProjectManager.getInstance().getOpenProjects()[0];


    private HAnSSingleton(){
        onUpdateObservers = new ArrayList<>();
        onDeleteObservers = new ArrayList<>();
        onAddObservers = new ArrayList<>();
        onInitObservers = new ArrayList<>();
        // TODO: do we have to wait for indexing first? could return null
        featureList = FeatureModelUtil.findFeatures(project);
        featureLocationManager = new FeatureLocationManager();
        registerObserver(featureLocationManager, NotifyOption.INITIALISATION);
    }

    public static HAnSSingleton getHAnSSingleton() {
        return hAnSSingleton;
    }

    public void registerObserver(HAnSObserverInterface observer, @NotNull NotifyOption option) {
        switch(option) {
            case UPDATE -> {
                if(!onUpdateObservers.contains(observer)) onUpdateObservers.add(observer);
                break;
            }
            case DELETE -> {
                if(!onDeleteObservers.contains(observer)) onDeleteObservers.add(observer);
                break;
            }
            case ADD -> {
                if(!onAddObservers.contains(observer)) onAddObservers.add(observer);
                break;
            }
            case INITIALISATION -> {
                if(!onInitObservers.contains(observer)) onInitObservers.add(observer);
                break;
            }
        }
    }
    // TODO: Proxy to hide functionality from other plugins
    public void notifyObservers(@NotNull NotifyOption option) {
        switch (option) {
            case UPDATE -> {
                for(HAnSObserverInterface observer:onUpdateObservers) {
                    observer.onUpdate();
                }
                break;
            }
            case DELETE -> {
                for(HAnSObserverInterface observer:onDeleteObservers) {
                    observer.onDelete();
                }
                break;
            }
            case ADD -> {
                for(HAnSObserverInterface observer:onAddObservers) {
                    observer.onAdd();
                }
                break;
            }
            case INITIALISATION -> {
                for(HAnSObserverInterface observer: onInitObservers) {
                    observer.onInit();
                }
                break;
            }
        }
    }

    public List<FeatureModelFeature> getFeatureList() {
        // TODO: Check this "Read access is allowed from inside read-action (or EDT) only"
        return FeatureModelUtil.findFeatures(project);
    }

    public Project getProject() {
        return project;
    }

    public List<Collection<PsiReference>> getPsiReferences() {
        return psiReferences;
    }

    public void setPsiReferences(List<Collection<PsiReference>> psiReferences) {
        this.psiReferences = psiReferences;
    }

    public HashMap<FeatureModelFeature, FeatureFileMapping> getFeatureMapping() {
        return featureMapping;
    }

    public void setFeatureMapping(HashMap<FeatureModelFeature, FeatureFileMapping> featureMapping) {
        this.featureMapping = featureMapping;
    }
}
