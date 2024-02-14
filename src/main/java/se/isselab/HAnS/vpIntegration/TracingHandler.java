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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
            String sourceFilePath = AssetsToClone.subAssetTrace;
            String[] fileOrDirName = sourceFilePath.split("/");
            targetFilePath = targetFilePath + "/" + fileOrDirName[fileOrDirName.length - 1];
            String updatedContent = sourceFilePath + targetFilePath + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(updatedContent);
            storeFeaturesTrace(targetFilePath, bufferFileWriter);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeFeaturesTrace(String targetPath, BufferedWriter bufferedWriter) {
        if(AssetsToClone.subFeatureTrace != null){
            try {
                for(String feature : AssetsToClone.subFeatureTrace){
                    String[] targetPathWithFeature = feature.split("/");
                    String featureTrace = feature + targetPath.substring(0, targetPath.length() - 1) + "/" + targetPathWithFeature[targetPathWithFeature.length - 1] + getCurrentDateAndTime() ;
                    bufferedWriter.newLine();
                    bufferedWriter.append(featureTrace);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            String sourceFilePath = AssetsToClone.subAssetTrace;
            String[] classOrMethodName = sourceFilePath.split("/");
            String targetPath = targetFilePath + getCurrentClassName(editor) + "/" + classOrMethodName[classOrMethodName.length - 1];
            String updatedContent = AssetsToClone.subAssetTrace + targetPath + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(updatedContent);
            storeFeaturesTrace(targetPath, bufferFileWriter);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentClassName(Editor editor){
        if(AssetsToClone.clonedMethod != null){
            PsiFile psiFile = PsiDocumentManager.getInstance(anActionEvent.getProject()).getPsiFile(editor.getDocument());
            int offset = editor.getCaretModel().getOffset();
            PsiElement elementAt = psiFile.findElementAt(offset);
            PsiClass containingClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
            return "/" + containingClass.getName();
        }
        return "";
    }

    private String getAssetName() {
        if(AssetsToClone.clonedClass != null){
            return AssetsToClone.clonedClass.getName();
        }
        if(AssetsToClone.clonedMethod != null){
            PsiClass parentClass = PsiTreeUtil.getParentOfType(AssetsToClone.clonedMethod, PsiClass.class);
            return parentClass.getName() + "/" + AssetsToClone.clonedMethod.getName();
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

    public void createFeatureTraces(){
        List<String> features = FeaturesCodeAnnotations.getInstance().getFeatureNames();
        if(features.size() != 0){
            AssetsToClone.subFeatureTrace = new ArrayList<String>();
            for(String feature : features){
                String subFeatureTrace = AssetsToClone.subAssetTrace.substring(0, AssetsToClone.subAssetTrace.length() - 1) + "/" + feature + "::";
                AssetsToClone.subFeatureTrace.add(subFeatureTrace);
            }
        }

    }

}
