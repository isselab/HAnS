package se.isselab.HAnS.singleton;

import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureExtension.HAnSObserverInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Acts as an observable and registers and notifies observers that implement {@link HAnSObserverInterface}.
 * @see HAnSObserverInterface
 */
public class FeatureSubject {

    private static final FeatureSubject featureSubject = new FeatureSubject();
    private List<HAnSObserverInterface> onUpdateObservers;
    private List<HAnSObserverInterface> onDeleteObservers;
    private List<HAnSObserverInterface> onAddObservers;
    private List<HAnSObserverInterface> onInitObservers;

    /**
     * Singleton class -> There is only one FeatureSubject in this project.
     */
    private FeatureSubject () {
        onUpdateObservers = new ArrayList<>();
        onDeleteObservers = new ArrayList<>();
        onAddObservers = new ArrayList<>();
        onInitObservers = new ArrayList<>();
    }

    public static FeatureSubject getFeatureSubject() {
        return featureSubject;
    }
    /**
     * Registers Observer that implement {@link HAnSObserverInterface}.
     * Adds observer to corresponding list, sorted by {@link NotifyOption}.
     * Method is protected from external plugins and can only be called by {@link HAnSManager}.
     * @see HAnSObserverInterface
     * @see NotifyOption
     * @see HAnSManager
     */
    protected void registerObserver(HAnSObserverInterface observer, @NotNull NotifyOption option) {
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
    /**
     * Notifies corresponding observers that implement {@link HAnSObserverInterface} and are listed with {@link NotifyOption}.
     * Method is protected from external plugins and can only be called by {@link HAnSManager}.
     */
    protected void notifyObservers(@NotNull NotifyOption option) {
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
}
