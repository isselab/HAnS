package se.isselab.HAnS.actions.assetsManagement;


import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.assetsManagement.cloningAssets.AssetsToClone;
import se.isselab.HAnS.assetsManagement.cloningAssets.CloningEditorMenuHandler;
import se.isselab.HAnS.assetsManagement.cloningAssets.CloningProjectMenuHandler;
import se.isselab.HAnS.assetsManagement.cloningAssets.TracingHandler;


public class CloneAsset extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String place = anActionEvent.getPlace();
        Project project = anActionEvent.getProject();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
        AssetsToClone.resetClones();
        if(place.equals("ProjectViewPopup")){
            //handle Project menu
            CloningProjectMenuHandler.handleProjectMenu(anActionEvent, project, tracingHandler);

        } else if(place.equals("EditorPopup")){
            //handle editor menu
            CloningEditorMenuHandler.handleEditorMenu(anActionEvent,editor, tracingHandler);
        }
    }
}