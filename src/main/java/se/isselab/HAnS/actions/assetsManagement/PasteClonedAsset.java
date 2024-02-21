package se.isselab.HAnS.actions.assetsManagement;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.assetsManagement.cloningAssets.AssetsToClone;
import se.isselab.HAnS.assetsManagement.cloningAssets.PastingEditorMenuHandler;
import se.isselab.HAnS.assetsManagement.cloningAssets.PastingProjectMenuHandler;
import se.isselab.HAnS.assetsManagement.cloningAssets.TracingHandler;


public class PasteClonedAsset extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String place = anActionEvent.getPlace();
        Project project = anActionEvent.getProject();
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
        if(place.equals("ProjectViewPopup")){
            //handle Project menu
            PastingProjectMenuHandler.handleProjectMenu(anActionEvent, project, tracingHandler);

        } else if(place.equals("EditorPopup")){
            //handle editor menu
            PastingEditorMenuHandler.handleEditorMenu(anActionEvent, project, tracingHandler);
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);
        e.getPresentation().setEnabledAndVisible(false);
        boolean pasteAssetVisibility = (AssetsToClone.clonedFile != null || AssetsToClone.clonedDirectory != null || AssetsToClone.clonedClass != null || AssetsToClone.clonedMethod != null || AssetsToClone.elementsInRange != null);
        e.getPresentation().setEnabledAndVisible(pasteAssetVisibility);
    }

}
