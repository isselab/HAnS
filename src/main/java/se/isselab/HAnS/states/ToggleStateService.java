package se.isselab.HAnS.states;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "ToggleState",
        storages = {@Storage("ToggleState.xml")}
)
public class ToggleStateService implements PersistentStateComponent<ToggleState> {
    private ToggleState state = new ToggleState();

    @Nullable
    @Override
    public ToggleState getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull ToggleState state) {
        this.state = state;
    }

    public static ToggleStateService getInstance() {
        return ServiceManager.getService(ToggleStateService.class);
    }
}