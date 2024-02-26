package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IntegrationStartUp implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            boolean directoryCloned = false;
            String sourceAssetPath = "";
            String targetAssetPath = "";
            String sourceProjectName = "";
            @Override
            public void before(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (event instanceof VFileCopyEvent) {
                        VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                        //System.out.println("Before file copy: " + copyEvent.getFile().getPath());
                        sourceAssetPath = copyEvent.getFile().getPath();
                        Project project = ProjectUtil.guessProjectForFile(copyEvent.getFile());
                        sourceProjectName = project != null ? project.getName() : "Unknown Project";
                        if(directoryCloned){
                            System.out.println("Before folder " + copyEvent.getFile().getParent().getPath());
                        }
                    } else if (event instanceof VFileCreateEvent) {
                        VFileCreateEvent createEvent = (VFileCreateEvent) event;
                        if (createEvent.isDirectory()) {
                            directoryCloned = true;
                        }
                    }
                }
            }
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (event instanceof VFileCopyEvent) {
                        VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                        //System.out.println("File copied : " + copyEvent.getFile().getName() + " to " + copyEvent.getPath());
                        targetAssetPath = copyEvent.getPath();
                        manageFileClone(copyEvent, project);
                    } else if (event instanceof VFileCreateEvent) {
                        VFileCreateEvent createEvent = (VFileCreateEvent) event;
                        if (createEvent.isDirectory()) {
                            //System.out.println("Folder copied: " + createEvent.getPath());
                            manageFolderClone(createEvent, project);
                        }
                    }
                }
            }
            private void manageFileClone(VFileCopyEvent copyEvent, Project project){
                PsiFile psiFile = PsiManager.getInstance(project).findFile(copyEvent.getFile());
                CloneManager.CloneFileAssets(project, psiFile, sourceProjectName, sourceAssetPath, targetAssetPath);
            }
            private void manageFolderClone(VFileCreateEvent createEvent, Project project) {

            }

        });
    }
}
