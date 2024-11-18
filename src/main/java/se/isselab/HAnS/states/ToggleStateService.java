package se.isselab.HAnS.states;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;

@State(
        name = "ToggleStateService",
        storages = {@Storage("toggleState.xml")} // XML file to persist state
)
public class ToggleStateService implements PersistentStateComponent<ToggleState> {

    private ToggleState state = new ToggleState(); // Default state

    public boolean isEnabled() {
        return state.isEnabled();
    }

    public void setEnabled(boolean enabled, @NotNull Project project) {
        state.setEnabled(enabled);

        // Force Refresh all open editors
        FileEditorManager.getInstance(project).getOpenFiles();
        EditorFactory.getInstance().refreshAllEditors();
    }

    @Nullable
    @Override
    public ToggleState getState() {
        return state; // Return the encapsulated state
    }

    @Override
    public void loadState(@NotNull ToggleState state) {
        this.state = state; // Load the persisted state
    }

    public static ToggleStateService getInstance(Project project) {
        return project.getService(ToggleStateService.class);
    }
}
