package se.isselab.HAnS.states;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;

@State(
        name = "ToggleStateService",
        storages = {@Storage("toggleState.xml")} // Best practice: lowercase file name
)
public class ToggleStateService implements PersistentStateComponent<ToggleStateService> {

    private boolean isEnabled = false; // State directly in the service

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    @Nullable
    @Override
    public ToggleStateService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ToggleStateService state) {
        this.isEnabled = state.isEnabled(); // Use getter to maintain encapsulation
    }

    public static ToggleStateService getInstance() {
        return ServiceManager.getService(ToggleStateService.class);
    }
}