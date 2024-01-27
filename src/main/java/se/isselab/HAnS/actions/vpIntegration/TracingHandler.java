package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TracingHandler {
    private AnActionEvent anActionEvent;

    public TracingHandler(AnActionEvent anActionEvent){
        this.anActionEvent = anActionEvent;
    }

    public void createFileOrFolderTrace(){
        try {
            String filePath = getTraceFilePath();
            try{
                String sourceFilePath = new String(Files.readAllBytes(Paths.get(filePath)));
            }catch(Exception e){
                File file = new File(filePath);
            }
            FileWriter fileWriter = new FileWriter(filePath, true);
            VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
            String content = virtualFile.getPath() + ";";
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(content);
            bufferFileWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void storeFileOrFolderTrace(VirtualFile targetVirtualFile){
        String targetFilePath = targetVirtualFile.getPath();
        String textFilePath = getTraceFilePath();
        String currentDateAndTime = getCurrentDateAndTime();
        try {
            String sourceFilePath = new String(Files.readAllBytes(Paths.get(textFilePath)));
            String[] pahtSplitted = sourceFilePath.split("/");
            String updatedContent = targetFilePath + "/" + pahtSplitted[pahtSplitted.length - 1] + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.append(updatedContent);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createCodeAssetsTrace(AnActionEvent anActionEvent){
        try {
            String filePath = getTraceFilePath();
            try{
                String sourceFilePath = new String(Files.readAllBytes(Paths.get(filePath)));
            }catch(Exception e){
                File file = new File(filePath);
            }
            Editor editor = FileEditorManager.getInstance(anActionEvent.getProject()).getSelectedTextEditor();
            FileWriter fileWriter = new FileWriter(filePath, true);
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
            String content = virtualFile.getPath() + "/" + getAssetName() +  ";";
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(content);
            bufferFileWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getAssetName() {
        if(CloneAssetCode.clonedClass != null){
            return CloneAssetCode.clonedClass.getName();
        }
        if(CloneAssetCode.clonedMethod != null){
            return CloneAssetCode.clonedMethod.getName();
        }
        return "Code Block";
    }

    private String getTraceFilePath(){
        return System.getProperty("user.home") + "\\Documents\\BA\\HAnS\\trace-db.txt";
    }

    public String getCurrentDateAndTime(){
        Date time = new Date();
        String date = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        String[] dateSplitted = date.split("/");
        String currentTime = (time.getTime() / 1000 / 60 / 60) % 24 + "" + (time.getTime() / 1000 / 60) % 60 + "" + (time.getTime() / 1000) % 60;
        String dateAndTimeInString = dateSplitted[2] + dateSplitted[0] + dateSplitted[1] + currentTime;
        return dateAndTimeInString;
    }

}
