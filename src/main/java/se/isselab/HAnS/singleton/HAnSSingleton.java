package se.isselab.HAnS.singleton;

import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureExtension.HAnSObserverInterface;

import java.util.ArrayList;
import java.util.List;

public class HAnSSingleton {
    private HAnSSingleton hAnSSingleton;
    private List<HAnSObserverInterface> onUpdateObservers;
    private List<HAnSObserverInterface> onDeleteObservers;
    private List<HAnSObserverInterface> onAddObservers;
    private List<HAnSObserverInterface> onInitialisationObservers;


    private HAnSSingleton(){
        onUpdateObservers = new ArrayList<>();
        onDeleteObservers = new ArrayList<>();
        onAddObservers = new ArrayList<>();
        onInitialisationObservers = new ArrayList<>();
    }

    public HAnSSingleton getHAnSSingleton() {
        if(hAnSSingleton == null) {
            hAnSSingleton = new HAnSSingleton();
        }

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
                if(!onInitialisationObservers.contains(observer)) onInitialisationObservers.add(observer);
                break;
            }
        }
    }

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
                for(HAnSObserverInterface observer:onInitialisationObservers) {
                    observer.onInitialisation();
                }
                break;
            }
        }
    }
}
