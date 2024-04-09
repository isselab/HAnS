package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class TracingHandler {

    public TracingHandler(){}


    public void storeCloneTrace(Project project, String sourceProjectName, String sourceFilePath, String targetFilePath){
        PathsMapping pathsMapping = PathsMapping.getInstance();
        String sourceFileRelativePath = getRelativePath(project, sourceFilePath, sourceProjectName);
        String targetFileRelativePath = getRelativePath(project, targetFilePath, project.getName());
        pathsMapping.paths.put(sourceFileRelativePath, sourceFilePath);
        pathsMapping.paths.put(targetFileRelativePath, targetFilePath);
        String currentDateAndTime = getCurrentDateAndTime();
        String textFilePath = getTraceFilePath(project);
        String test = System.getProperty("java.io.tmpdir") + File.separator + ".trace-db.txt";
        try {
            String updatedContent = sourceFileRelativePath + ";" +  targetFileRelativePath + ";" + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(updatedContent);
            storeCopyFeatureTraces(bufferFileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeCopyFeatureTraces(BufferedWriter bufferedWriter){
        if(AssetsAndFeatureTraces.subFeatureTrace != null){
            try {
                for(String feature : AssetsAndFeatureTraces.subFeatureTrace){
                    String featureTrace = feature;
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
        String path = cleanedUrls.get(0) + "/.trace-db.txt";
        return path;
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
                boolean isUnassigned = feature.contains("UNASSIGNED");
                String subFeatureTrace = !isUnassigned ? sourceProjectName + "::" + feature + ";" + targetProjectName + "::" + feature : sourceProjectName + "::" + feature.substring(12) + ";" + targetProjectName + "::" + feature;
                AssetsAndFeatureTraces.subFeatureTrace.add(subFeatureTrace);
            }
        }
    }

    public String getRelativePath(Project project, String path, String projectName) {
        if(project.getName().equals(projectName)){
            return path.substring(path.indexOf(projectName) + projectName.length() + 1);
        }
        return path.substring(path.indexOf(projectName));
    }

}
