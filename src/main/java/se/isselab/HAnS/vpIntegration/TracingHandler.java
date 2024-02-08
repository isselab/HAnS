package se.isselab.HAnS.vpIntegration;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.actions.vpIntegration.CloneAsset;

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

    public String createFileOrFolderTrace(){
        try {
            String filePath = getTraceFilePath(anActionEvent.getProject());
            try{
                String sourceFilePath = new String(Files.readAllBytes(Paths.get(filePath)));
            }catch(Exception e){
                File file = new File(filePath);
            }
            FileWriter fileWriter = new FileWriter(filePath, true);
            VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
            String content = virtualFile.getPath() + ";";
            return content;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void storeFileOrFolderTrace(){
        VirtualFile targetVirtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        String targetFilePath = targetVirtualFile.getPath();
        String textFilePath = getTraceFilePath(anActionEvent.getProject());
        String currentDateAndTime = getCurrentDateAndTime();
        try {
            String sourceFilePath = CloneAsset.subTrace;
            String[] pathSplitted = sourceFilePath.split("/");
            String updatedContent = sourceFilePath + targetFilePath + "/" + pathSplitted[pathSplitted.length - 1] + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(updatedContent);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String createCodeAssetsTrace(){
        try {
            String filePath = getTraceFilePath(anActionEvent.getProject());
            try{
                String sourceFilePath = new String(Files.readAllBytes(Paths.get(filePath)));
            }catch(Exception e){
                File file = new File(filePath);
            }
            Editor editor = FileEditorManager.getInstance(anActionEvent.getProject()).getSelectedTextEditor();
            FileWriter fileWriter = new FileWriter(filePath, true);
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
            String content = virtualFile.getPath() + "/" + getAssetName() +  ";";
            return content;

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void storeCodeAssetsTrace(){
        Editor editor = FileEditorManager.getInstance(anActionEvent.getProject()).getSelectedTextEditor();
        VirtualFile targetFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        String targetFilePath = targetFile.getPath();
        String textFilePath = getTraceFilePath(anActionEvent.getProject());
        String currentDateAndTime = getCurrentDateAndTime();
        try {
            String sourceFilePath = CloneAsset.subTrace;
            String[] pathSplitted = sourceFilePath.split("/");
            String updatedContent = CloneAsset.subTrace + targetFilePath + getCurrentClassName(editor) + "/" + pathSplitted[pathSplitted.length - 1] + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(updatedContent);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentClassName(Editor editor){
        if(CloneAsset.clonedMethod != null){
            PsiFile psiFile = PsiDocumentManager.getInstance(anActionEvent.getProject()).getPsiFile(editor.getDocument());
            int offset = editor.getCaretModel().getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);
            PsiClass containingClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
            return "/" + containingClass.getName();
        }
        return "";
    }

    private String getAssetName() {
        if(CloneAsset.clonedClass != null){
            return CloneAsset.clonedClass.getName();
        }
        if(CloneAsset.clonedMethod != null){
            PsiClass parentClass = PsiTreeUtil.getParentOfType(CloneAsset.clonedMethod, PsiClass.class);
            return parentClass.getName() + "/" + CloneAsset.clonedMethod.getName();
        }
        return "CodeBlock";
    }

    private String getTraceFilePath(Project project){
        //return System.getProperty("user.home") + "\\Documents\\BA\\HAnS\\trace-db.txt";
        if (project == null) return null;

        VirtualFile projectBaseDir = project.getBaseDir();
        if (projectBaseDir == null) return null;

        return projectBaseDir.getPath() + "/trace-db.txt";
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
