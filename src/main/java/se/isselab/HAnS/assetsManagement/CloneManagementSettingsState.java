package se.isselab.HAnS.assetsManagement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
@State(
        name = "se.isselab.HAnS.assetsManagement.CloneManagementSettingsState",
        storages = @Storage("plugin.xml")
)
public class CloneManagementSettingsState implements PersistentStateComponent<CloneManagementSettingsState> {
    String prefKey = "All";
    @Override
    public @Nullable CloneManagementSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull CloneManagementSettingsState state) {

    }
    static CloneManagementSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(CloneManagementSettingsState.class);
    }
}
