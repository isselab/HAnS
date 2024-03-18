package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TracingHandler {

    public TracingHandler(){}


    public void storeCopyPasteFileTrace(Project project, String sourceFilePath, String targetFilePath){
        String currentDateAndTime = getCurrentDateAndTime();
        String textFilePath = getTraceFilePath(project);
        try {
            String updatedContent = sourceFilePath + ";" +  targetFilePath + ";" + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(updatedContent);
            storeCopyFeatureTraces(currentDateAndTime, bufferFileWriter);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void storeCopyFeatureTraces(String version, BufferedWriter bufferedWriter){
        if(AssetsAndFeatureTraces.subFeatureTrace != null){
            try {
                for(String feature : AssetsAndFeatureTraces.subFeatureTrace){
                    String featureTrace = feature + ";" + version;
                    bufferedWriter.newLine();
                    bufferedWriter.append(featureTrace);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getTraceFilePath(Project project){
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
        int hours = (int)((time.getTime() / 1000 / 60 / 60) + 1) % 24;
        int minutes = (int)(time.getTime() / 1000 / 60) % 60;
        int seconds = (int)(time.getTime() / 1000) % 60;
        String currentTime = String.format("%02d%02d%02d", hours, minutes, seconds);
        String dateAndTimeInString = dateSplitted[2] + dateSplitted[0] + dateSplitted[1] + currentTime;
        return dateAndTimeInString;
    }


    public void createCopyFeatureTrace(Project project, String sourceProjectName, String targetProjectName){
        List<String> features = FeaturesCodeAnnotations.getInstance().getFeatureNames();
        if(features.size() != 0){
            AssetsAndFeatureTraces.subFeatureTrace = new ArrayList<String>();
            for(String feature : features){
                String subFeatureTrace = sourceProjectName + "::" + feature + ";" + targetProjectName + "::" + feature;
                AssetsAndFeatureTraces.subFeatureTrace.add(subFeatureTrace);
            }
        }
    }

}
