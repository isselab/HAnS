package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.fileEditor.FileEditor;
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
import se.isselab.HAnS.assetsManagement.AssetsManagementPreferences;

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
        System.out.println(file.getPath());
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        boolean cloned = isCloned(psiFile);
        boolean isSourceFileChanged = isSourceFileChanged(file);
        if(AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("propagate")
          || AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("both")) {
            if (cloned && isSourceFileChanged) {
                EditorNotificationPanel panel = new EditorNotificationPanel();
                panel.setText("This file has been cloned and some changes have been made to the source file. Please check the changes for consistency.");
                panel.createActionLabel("Cancel", () -> {
                    panel.setVisible(false);
                });
                return panel;
            }
        }
        return null;
    }

    private boolean isSourceFileChanged(VirtualFile file) {
        List<List<String>> parsedLines = getTraces();
        for(int i = 0; i < parsedLines.size(); i++){
            if(parsedLines.get(i).get(1).equals(file.getPath()))
            {
                VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(parsedLines.get(i).get(0));
                if(sourceFile != null){
                    String lastTimeModification = getLastModificationTime(sourceFile);
                    if(Long.parseLong(lastTimeModification) > Long.parseLong(parsedLines.get(i).get(2))) return true;
                }
            }
        }
        return false;
    }

    private boolean isCloned(PsiFile file) {
        List<List<String>> parsedLines = getTraces();
        for(int i = 0; i < parsedLines.size(); i++){
            if(parsedLines.get(i).get(1).equals(file.getVirtualFile().getPath()))
                return true;
        }
        return false;
    }

    public static void fileIsChanged(VirtualFile sourceFile){
        if(AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("propagate")
          || AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("both")) {
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
                            parsedLines.add(parts);
                        }
                    }
                }
            }
        }
        return parsedLines;
    }

    private static Project findProjectForVirtualFile(VirtualFile file) {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
            if (ProjectRootManager.getInstance(project).getFileIndex().isInContent(file)) {
                return project;
            }
        }
        return null;
    }

    public String getLastModificationTime(VirtualFile virtualFile) {
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
