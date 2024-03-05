package se.isselab.HAnS.assetsManagement.propagatingToAsset;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropagatingProvider extends EditorNotifications.Provider<EditorNotificationPanel>{
    private final Project myProject;
    public PropagatingProvider(Project project){
        myProject = project;
    }
    @Override
    public @NotNull Key<EditorNotificationPanel> getKey() {
        return Key.create("editor.panel.notification");
    }
    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        boolean cloned = isCloned(psiFile);
        if (cloned) {
            EditorNotificationPanel panel = new EditorNotificationPanel();
            panel.setText("This file was copied/cloned. Click on Propagate to get the changes from the source file.");
            panel.createActionLabel("Propagate Changes", () -> {
                propagateChanges();
            });
            panel.createActionLabel("Cancel", () -> {
                panel.setVisible(false);
            });
            return panel;
        }
        return null;
    }

    private boolean isCloned(PsiFile file) {
        //TODO check if file is cloned
        return true;
    }
    private void propagateChanges(){
        //TODO propagate changes of file
    }
}
