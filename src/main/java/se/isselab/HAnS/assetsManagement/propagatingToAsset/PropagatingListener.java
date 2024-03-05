package se.isselab.HAnS.assetsManagement.propagatingToAsset;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.fileEditor.ex.FileEditorWithProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PropagatingListener implements FileEditorManagerListener {
    @Override
    public void fileOpenedSync(@NotNull FileEditorManager source, @NotNull VirtualFile file, @NotNull List<FileEditorWithProvider> editorsWithProviders) {
        Project myProject = ProjectManager.getInstance().getDefaultProject();
        PropagatingService propagatingService = new PropagatingService(myProject);
        propagatingService.ServiceMethod(file);
        System.out.println("File opened");
    }
}
