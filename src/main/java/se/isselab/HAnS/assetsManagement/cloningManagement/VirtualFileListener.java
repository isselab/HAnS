package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
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
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.assetsManagement.AssetsManagementPreferences;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VirtualFileListener implements StartupActivity {
    private static final long DEBOUNCE_DELAY_MS = 500;
    private long lastEventTime = 0;
    @Override
    public void runActivity(@NotNull Project project) {
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
            boolean directoryCloned = false;
            boolean fileInDirectoryCloned = false;
            PsiFile sourceFile;
            String sourceAssetPath = "";
            String targetAssetPath = "";
            String sourceProjectName = "";
            /**
             * gets the path of the file from the source location when the paste action is triggered
             * sets source asset paths
             * sets directoryCloned to true to disable creating traces for subfiles in the cloned directory
             * **/
            @Override
            public void before(@NotNull List<? extends VFileEvent> events) {
                for (VFileEvent event : events) {
                    if (event instanceof VFileCopyEvent) {
                        if(!directoryCloned){
                            VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                            sourceAssetPath = copyEvent.getFile().getPath();
                            sourceProjectName = getSourceProjectName(copyEvent);
                            sourceFile = PsiManager.getInstance(project).findFile(copyEvent.getFile());
                        } else {
                            VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                            String sourceFilePath = copyEvent.getFile().getPath();
                            String[] fileSplitted = sourceFilePath.split("/");
                            sourceAssetPath = buildSourceDirectoryPath(fileSplitted);
                            sourceProjectName = getSourceProjectName(copyEvent);
                            fileInDirectoryCloned = true;
                        }

                    } else if (event instanceof VFileCreateEvent) {
                        VFileCreateEvent createEvent = (VFileCreateEvent) event;
                        if (createEvent.isDirectory()) {
                            String directoryName = createEvent.getChildName();
                            boolean directoryFound = directoryIsCloned(directoryName);
                            if(directoryFound)
                                directoryCloned = true;
                        }
                    }
                }
            }
            /**
             * gets the path of the file from the target location when the paste action is triggered
             * sets target asset path
             * sets directoryCloned to false for creating traces for copied files again
             * **/
            @Override
            public void after(@NotNull List<? extends VFileEvent> events) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastEventTime > DEBOUNCE_DELAY_MS) {
                    lastEventTime = currentTime;
                    for (VFileEvent event : events) {
                        if (event instanceof VFileCopyEvent) {
                            if(!directoryCloned){
                                VFileCopyEvent copyEvent = (VFileCopyEvent) event;
                                targetAssetPath = copyEvent.getPath();
                                manageFileClone(targetAssetPath);
                            }
                        }  else if (event instanceof VFileCreateEvent) {
                            VFileCreateEvent createEvent = (VFileCreateEvent) event;
                            if (createEvent.isDirectory()) {
                                if(directoryCloned){
                                    targetAssetPath = createEvent.getPath();
                                    // toggle directory cloned to false after all file copies in the directory were checked and no traces were created for them
                                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                                    scheduler.schedule(() -> {
                                        directoryCloned = false;
                                        manageFolderClone();
                                        scheduler.shutdown();
                                    }, 2, TimeUnit.SECONDS);
                                }
                            }
                        }
                        else if(event instanceof VFileContentChangeEvent){
                            NotificationProvider.fileIsChanged(event.getFile());
                        }
                    }
                }
            }
            private void manageFileClone(String targetAssetPath) {
                if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isClonePreference() || AssetsAndFeatureTraces.isCloneAndPropagatePreference() || AssetsAndFeatureTraces.isCloneAndShowClonePreference()){
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

            }
            /**
             * create folder trace only if that folder was found in the same and other instaces and content from it was cloned
             * */
            private void manageFolderClone() {
                if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isClonePreference() || AssetsAndFeatureTraces.isCloneAndPropagatePreference() || AssetsAndFeatureTraces.isCloneAndShowClonePreference()){
                    if(fileInDirectoryCloned){
                        VirtualFile sourceDirectory = LocalFileSystem.getInstance().findFileByPath(sourceAssetPath);
                        PsiDirectory psiDirectory = findPsiDirectory(project, sourceDirectory);
                        VirtualFile targetVirtualFile = LocalFileSystem.getInstance().findFileByPath(targetAssetPath);
                        if (targetVirtualFile != null) {
                            Project targetProject = ProjectUtil.guessProjectForFile(targetVirtualFile);
                            if (targetProject != null) {
                                CloneManager.CloneFolderAssets(targetProject, psiDirectory, sourceProjectName, sourceAssetPath, targetAssetPath);
                            } else {
                                System.out.println("No project found");
                            }
                        }
                    }
                    fileInDirectoryCloned = false;
                }

            }

            private PsiDirectory findPsiDirectory(Project project, VirtualFile dir) {
                final PsiDirectory[] psiDirectoryHolder = new PsiDirectory[1];
                ApplicationManager.getApplication().runReadAction(new Runnable() {
                    @Override
                    public void run() {
                        psiDirectoryHolder[0] = PsiManager.getInstance(project).findDirectory(dir);
                    }
                });
                return psiDirectoryHolder[0];
            }

            private String buildSourceDirectoryPath(String[] filePath){
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < filePath.length - 1; i++) {
                    sb.append(filePath[i]);
                    if (i < filePath.length - 2) {
                        sb.append("/");
                    }
                }
                return sb.toString();
            }

            private boolean directoryIsCloned(String directoryName){
                Project[] projects = ProjectManager.getInstance().getOpenProjects();
                for(Project project : projects){
                    VirtualFile baseDir = project.getBaseDir();
                    if (baseDir != null && baseDir.isDirectory()) {
                        if (directoryExistsInProject(baseDir, directoryName)) {
                            return true;
                        }
                    }
                }
                return false;
            }
            private String getSourceProjectName(VFileCopyEvent copyEvent){
                Project project = ProjectUtil.guessProjectForFile(copyEvent.getFile());
                return project != null ? project.getName() : "Unknown Project";
            }

            private boolean directoryExistsInProject(VirtualFile directory, String directoryName) {
                if (directory.getName().equals(directoryName)) {
                    return true;
                }
                for (VirtualFile child : directory.getChildren()) {
                    if (child.isDirectory()) {
                        if (directoryExistsInProject(child, directoryName)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }
}
