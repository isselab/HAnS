package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

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
            bufferFileWriter.newLine();
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
        /** Alternative solution for getting the base directory*/
        /* List<String> projectUrls = new ArrayList<>();
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(file -> {
            System.out.println("File: " + file.getPath());
            projectUrls.add(file.getPath());
            return true;
        });*/
        List<String> vFiles = ProjectRootManager.getInstance(project).getContentRootUrls();
        List<String> cleanedUrls = new ArrayList<>();
        for (String url : vFiles) {
            String cleanedUrl = url.replaceFirst("^file://", "");
            cleanedUrls.add(cleanedUrl);
        }
        return cleanedUrls.get(0) + "/trace-db.txt";
    }

    public String getCurrentDateAndTime(){
        Date time = new Date();
        String date = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        String[] dateSplitted = date.split("/");
        int hours = (int)((time.getTime() / 1000 / 60 / 60) + 1) % 24;
        int minutes = (int)(time.getTime() / 1000 / 60) % 60;
        int seconds = (int)(time.getTime() / 1000) % 60;
        String currentTime = String.format("%02d%02d%02d", hours, minutes, seconds);
        return dateSplitted[2] + dateSplitted[0] + dateSplitted[1] + currentTime;
    }


    public void createCopyFeatureTrace(String sourceProjectName, String targetProjectName){
        List<String> features = FeaturesCodeAnnotations.getInstance().getFeatureNames();
        if(!features.isEmpty()){
            AssetsAndFeatureTraces.subFeatureTrace = new ArrayList<>();
            for(String feature : features){
                String subFeatureTrace = sourceProjectName + "::" + feature + ";" + targetProjectName + "::" + feature;
                AssetsAndFeatureTraces.subFeatureTrace.add(subFeatureTrace);
            }
        }
    }

}
