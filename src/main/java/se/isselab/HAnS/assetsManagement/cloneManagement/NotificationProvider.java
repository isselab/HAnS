package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.*;

public class NotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel>{
    @Override
    public @NotNull Key<EditorNotificationPanel> getKey() {
        return Key.create("editor.panel.notification");
    }
    @Nullable
    @Override
    public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull FileEditor fileEditor, @NotNull Project project) {
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        boolean cloned = isCloned(psiFile);
        boolean isSourceFileChanged = isSourceFileChanged(file);
        if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isShowClonePreference() || AssetsAndFeatureTraces.isShowCloneAndPropagatePreference() || AssetsAndFeatureTraces.isCloneAndShowClonePreference()) {
            if (cloned && !isSourceFileChanged) {
                String sourceFilePath = getSourcePath(file);
                EditorNotificationPanel panel = new EditorNotificationPanel();
                panel.setText("This file has been cloned. To show the source file click on Show Source");
                panel.createActionLabel("Show Source File", () -> {
                    VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(sourceFilePath);
                    if (sourceFile != null && project != null) {
                        FileEditorManager.getInstance(project).openFile(sourceFile, true);
                        panel.setVisible(false);
                    } else {
                        System.out.println("file was not found");
                    }
                });
                panel.createActionLabel("Hide", () -> {
                    panel.setVisible(false);
                });
                return panel;
            }
        }
        if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isPropagatePreference() || AssetsAndFeatureTraces.isCloneAndPropagatePreference() || AssetsAndFeatureTraces.isShowCloneAndPropagatePreference()) {
            if (cloned && isSourceFileChanged) {
                EditorNotificationPanel panel = new EditorNotificationPanel();
                panel.setText("This file has been cloned and some changes have been made to the source file. Please check the changes for consistency.");
                panel.createActionLabel("Merge Changes", () -> {
                    String sourceFilePath = getSourcePath(file);
                    VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(sourceFilePath);
                    openMergeWindow(project, sourceFile, file);
                    panel.setVisible(false);
                });
                panel.createActionLabel("Hide", () -> {
                    panel.setVisible(false);
                });
                return panel;
            }
        }
        return null;
    }

    public boolean isSourceFileChanged(VirtualFile file) {
        List<List<String>> parsedLines = getTraces();
        for (int i = 0; i < parsedLines.size(); i++) {
            if (parsedLines.get(i).get(1).equals(file.getPath())) {
                VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(parsedLines.get(i).get(0));
                if (sourceFile != null) {
                    String lastTimeModification = getLastModificationTime(sourceFile);
                    if (Long.parseLong(lastTimeModification) > Long.parseLong(parsedLines.get(i).get(2))) return true;
                }
            }
        }
        return false;
    }
    public void openMergeWindow(Project project, VirtualFile sourceFile, VirtualFile clonedFile) {
        DiffContentFactory contentFactory = DiffContentFactory.getInstance();
        DiffContent sourceFileContent = contentFactory.create(project, sourceFile);
        DiffContent clonedFileContent = contentFactory.create(project, clonedFile);
        SimpleDiffRequest diffRequest = new SimpleDiffRequest("Merge Changes", sourceFileContent, clonedFileContent, "Source File", "Cloned File");
        DiffManager.getInstance().showDiff(project, diffRequest);
    }

    public static String getSourcePath(VirtualFile file) {
        List<List<String>> parsedLines = getTraces();
        for(int i = 0; i < parsedLines.size(); i++){
            if(parsedLines.get(i).get(1).equals(file.getPath()))
            {
                return parsedLines.get(i).get(0);
            }
        }
        return null;
    }

    public boolean isCloned(PsiFile file) {
        List<List<String>> parsedLines = getTraces();
        for(int i = 0; i < parsedLines.size(); i++){
            if(parsedLines.get(i).get(1).equals(file.getVirtualFile().getPath()))
                return true;
        }
        return false;
    }

    public static void fileIsChanged(VirtualFile sourceFile){
        if(AssetsAndFeatureTraces.isAllPreference() || AssetsAndFeatureTraces.isPropagatePreference() || AssetsAndFeatureTraces.isCloneAndPropagatePreference() || AssetsAndFeatureTraces.isShowCloneAndPropagatePreference()) {
            List<List<String>> parsedLines = getTraces();
            for(int i = 0; i < parsedLines.size(); i++){
                if(parsedLines.get(i).get(0).equals(sourceFile.getPath()))
                {
                    VirtualFile clonedFile = LocalFileSystem.getInstance().findFileByPath(parsedLines.get(i).get(1));
                    if(clonedFile != null) {
                        Project targetProject = findProjectForVirtualFile(clonedFile);
                        assert targetProject != null : "targetProject should not be null";
                        EditorNotifications.getInstance(targetProject).updateNotifications(clonedFile);
                        return;
                    }
                }
            }
        }
    }
    public static List<List<String>> getTraces(){
        PathsMapping pathsMapping = PathsMapping.getInstance();
        List<List<String>> parsedLines = new ArrayList<>();
        Project[] projects = ProjectManager.getInstance().getOpenProjects();
        for(Project project : projects){
            String traceFilePath = TracingHandler.getTraceFilePath(project);
            VirtualFile traceFile = LocalFileSystem.getInstance().findFileByPath(traceFilePath);
            if(traceFile != null){
                PsiFile traceDBFile = PsiManager.getInstance(project).findFile(traceFile);
                if(traceDBFile != null) {
                    String[] lines = traceDBFile.getText().split("\n");
                    for (String line : lines) {
                        if (line.contains(";")) {
                            List<String> parts = Arrays.asList(line.split(";"));
                            String source = pathsMapping.paths.get(parts.get(0));
                            String target = pathsMapping.paths.get(parts.get(1));
                            if(source != null && target != null){
                                parts.set(0, source);
                                parts.set(1, target);
                            }
                            parsedLines.add(parts);
                        }
                    }
                }
            }
        }
        return parsedLines;
    }

    public static Project findProjectForVirtualFile(VirtualFile file) {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (ProjectRootManager.getInstance(project).getFileIndex().isInContent(file)) {
                return project;
            }
        }
        return null;
    }

    public static String getLastModificationTime(VirtualFile virtualFile) {
        long timestamp = virtualFile.getTimeStamp();
        Date fileDate = new Date(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(fileDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fileDate);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        String formattedTime = String.format("%02d%02d%02d", hours, minutes, seconds);

        return formattedDate + formattedTime;
    }
}
