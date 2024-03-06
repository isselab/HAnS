package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.assetsManagement.propagatingToAsset.PropagatingProvider;

import java.util.List;

public class CloneFileListener implements StartupActivity {
    private static final long DEBOUNCE_DELAY_MS = 500;
    private long lastEventTime = 0;
    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            boolean directoryCloned = false;
            PsiFile sourceFile;
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
                        sourceFile = PsiManager.getInstance(project).findFile(copyEvent.getFile());

                    } else if (event instanceof VFileCreateEvent) {
                        VFileCreateEvent createEvent = (VFileCreateEvent) event;
                        String newChildPath = createEvent.getChildName(); // Get the name of the created file or directory
                        VirtualFile parentDirectory = createEvent.getParent();
                        String fullPath = parentDirectory.getPath() + "/" + newChildPath;

                        if (createEvent.isDirectory()) {
                            System.out.println("Directory created (possibly by copy): " + fullPath);
                        } else {
                            System.out.println("File created (possibly by copy): " + fullPath);
                        }
                    }
                }
            }
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastEventTime > DEBOUNCE_DELAY_MS) {
                    lastEventTime = currentTime;
                    for (VFileEvent event : events) {
                        if (event instanceof VFileCopyEvent) {
                            VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                            //System.out.println("File copied : " + copyEvent.getFile().getName() + " to " + copyEvent.getPath());
                            targetAssetPath = copyEvent.getPath();
                            manageFileClone(targetAssetPath);

                        }  else if (event instanceof VFileCreateEvent) {
                            VFileCreateEvent createEvent = (VFileCreateEvent) event;
                            if (createEvent.isDirectory()) {
                                //System.out.println("Folder copied: " + createEvent.getPath());
                                manageFolderClone(createEvent, project);
                            }
                        }
                        else if(event instanceof VFileContentChangeEvent){
                            PropagatingProvider.fileIsChanged(project, event.getFile());
                            System.out.println("One File was changed " + event.getFile().getPath());
                        }
                    }
                }
            }
            private void manageFileClone(String targetAssetPath) {
                VirtualFile targetVirtualFile = LocalFileSystem.getInstance().findFileByPath(targetAssetPath);
                if (targetVirtualFile != null) {
                    Project targetProject = ProjectUtil.guessProjectForFile(targetVirtualFile);
                    if (targetProject != null) {
                        CloneManager.CloneFileAssets(targetProject, sourceFile, sourceProjectName, sourceAssetPath, targetAssetPath);
                    } else {
                        System.out.println("No project found");
                    }
                }
            }
            private void manageFolderClone(VFileCreateEvent createEvent, Project project) {
                CloneManager.CloneFolderAssets();
            }
        });
    }
}
