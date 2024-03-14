package se.isselab.HAnS.assetsManagement.propagatingToAsset;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.assetsManagement.AssetsManagementSettings;
import se.isselab.HAnS.assetsManagement.cloningAssets.TracingHandler;

import java.text.SimpleDateFormat;
import java.util.*;

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
        System.out.println(file.getPath());
        PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        boolean cloned = isCloned(psiFile);
        boolean isSourceFileChanged = isSourceFileChanged(project, file);
        if(AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("propagate")
          || AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("both")) {
            if (cloned && isSourceFileChanged) {
                EditorNotificationPanel panel = new EditorNotificationPanel();
                panel.setText("This file is copied/cloned and some changes has been made to the source file. Click on Propagate to get the changes from the source file.");
                panel.createActionLabel("Propagate Changes", () -> {
                    propagateChanges(psiFile);
                });
                panel.createActionLabel("Cancel", () -> {
                    panel.setVisible(false);
                });
                return panel;
            }
        }
        return null;
    }

    private boolean isSourceFileChanged(Project project, VirtualFile file) {
        List<List<String>> parsedLines = getTraces(project);
        for(int i = 0; i < parsedLines.size(); i++){
            if(parsedLines.get(i).get(1).equals(file.getPath()))
            {
                VirtualFile sourceFile = LocalFileSystem.getInstance().findFileByPath(parsedLines.get(i).get(0));
                String lastTimeModification = formatFileModificationTime(sourceFile);
                if(Long.parseLong(lastTimeModification) > Long.parseLong(parsedLines.get(i).get(2))) return true;
            }
        }
        return false;
    }

    private boolean isCloned(PsiFile file) {
        Project project = file.getProject();
        List<List<String>> parsedLines = getTraces(project);
        for(int i = 0; i < parsedLines.size(); i++){
            if(parsedLines.get(i).get(1).equals(file.getVirtualFile().getPath()))
                return true;
        }
        return false;
    }
    private void propagateChanges(PsiFile file){
        //TODO propagate changes to file
    }

    public static void fileIsChanged(Project project, VirtualFile sourceFile){
        if(AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("propagate")
          || AssetsManagementSettings.properties.getValue(AssetsManagementSettings.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("both")) {
            List<List<String>> parsedLines = getTraces(project);
            for(int i = 0; i < parsedLines.size(); i++){
                if(parsedLines.get(i).get(0).equals(sourceFile.getPath()))
                {
                    VirtualFile clonedFile = LocalFileSystem.getInstance().findFileByPath(parsedLines.get(i).get(1));
                    EditorNotifications.getInstance(project).updateNotifications(clonedFile);
                    return;
                }
            }
        }
    }
    public static List<List<String>> getTraces(Project project){
        List<List<String>> parsedLines = new ArrayList<>();
        String traceFilePath = TracingHandler.getTraceFilePath(project);
        VirtualFile traceFile = LocalFileSystem.getInstance().findFileByPath(traceFilePath);
        if(traceFile.exists()){
            PsiFile traceDBFile = PsiManager.getInstance(project).findFile(traceFile);
            String[] lines = traceDBFile.getText().split("\n");
            for (String line : lines) {
                if (line.contains(";")) {
                    List<String> parts = Arrays.asList(line.split(";"));
                    parsedLines.add(parts);
                }
            }
        }
        return parsedLines;
    }

    public String formatFileModificationTime(VirtualFile virtualFile) {
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
