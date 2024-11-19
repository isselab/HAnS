/*
Copyright 2024 Ahmad Al Shihabi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.dvcs.repo.Repository;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.ini4j.Config;

import java.io.*;
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
        try {
            String userName = getGitUserName();
            String updatedContent = sourceFileRelativePath + ";" +  targetFileRelativePath + ";" + currentDateAndTime + userName;
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

    public void storeCopyFeatureTraces(BufferedWriter bufferedWriter){
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

    public static String getGitUserName() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("git", "config", "user.name");
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        StringBuilder output = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            output.append(line);
        }
        if (output.toString().isEmpty()) {
            return ";#unknown";
        } else {
            return ";#" + output.toString();
        }
    }

    public static String getTraceFilePath(Project project){
        if (project == null) return null;
        /** Alternative solutions for getting the base directory*/
        /* List<String> projectUrls = new ArrayList<>();
        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(file -> {
            System.out.println("File: " + file.getPath());
            projectUrls.add(file.getPath());
            return true;
        });*/
        /////////////////////////////////////////////////////////
        /*
        List<String> vFiles = ProjectRootManager.getInstance(project).getContentRootUrls();
        List<String> cleanedUrls = new ArrayList<>();
        for (String url : vFiles) {
            String cleanedUrl = url.replaceFirst("^file://", "");
            cleanedUrls.add(cleanedUrl);
        }
        String path = cleanedUrls.get(0) + "/.trace-db.txt";

         */
        return project.getBasePath() + "/.trace-db.txt" ;
    }

    public String getCurrentDateAndTime(){
        Date time = new Date();
        String date = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        String[] dateSplitted = date.split("/");
        int hours = (int)((time.getTime() / 1000 / 60 / 60) + 2) % 24;
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
