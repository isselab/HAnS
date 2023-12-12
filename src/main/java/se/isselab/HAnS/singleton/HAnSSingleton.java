package se.isselab.HAnS.singleton;

import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureExtension.HAnSObserverInterface;

import java.util.ArrayList;
import java.util.List;

public class HAnSSingleton {
    private static final HAnSSingleton hAnSSingleton = new HAnSSingleton();
    private List<HAnSObserverInterface> onUpdateObservers;
    private List<HAnSObserverInterface> onDeleteObservers;
    private List<HAnSObserverInterface> onAddObservers;
    private List<HAnSObserverInterface> onInitObservers;


    private HAnSSingleton(){
        onUpdateObservers = new ArrayList<>();
        onDeleteObservers = new ArrayList<>();
        onAddObservers = new ArrayList<>();
        onInitObservers = new ArrayList<>();
    }

    public HAnSSingleton getHAnSSingleton() {
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
}
