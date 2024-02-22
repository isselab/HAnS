package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileCopyEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IntegrationStartUp implements StartupActivity {
    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            boolean directoryCloned = false;
            @Override
            public void before(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (event instanceof VFileCopyEvent) {
                        VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                        System.out.println("Before file copy: " + copyEvent.getFile().getPath());
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
                        System.out.println("File copied : " + copyEvent.getFile().getName() + " to " + copyEvent.getPath());
                    } else if (event instanceof VFileCreateEvent) {
                        VFileCreateEvent createEvent = (VFileCreateEvent) event;
                        if (createEvent.isDirectory()) {
                            System.out.println("Folder copied: " + createEvent.getPath());
                        }
                    }
                }
            }
        });

    }
}
