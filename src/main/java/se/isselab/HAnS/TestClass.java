package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.application.CoroutinesKt;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.singleton.HAnSSingleton;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        HAnSSingleton singleton = HAnSSingleton.getHAnSSingleton();

        startBackgroundTask(singleton);


        
        // TODO THESIS: find a way to start background process -> commented code below is working, but there is an UI freeze
        // TODO THESIS: do we need this and where do we need this?
//        FeatureLocationManager featureLocationManager = new FeatureLocationManager(e.getProject());
//
//        for(var feature : FeatureModelUtil.findFeatures(e.getProject())){
//            System.out.println("Checking: " + feature.getLPQText());
//            var mapping = featureLocationManager.getFeatureFileMapping(feature);
//            var map = mapping.getAllFeatureLocations();
//            for(var path : map.keySet()){
//                System.out.println("    File: " + path);
//                for(var block : map.get(path)){
//                    System.out.println("        Start: " + (block.getStartLine() + 1) + "\n        End: " + (block.getEndLine() + 1) + "\n        Total: " + block.getLineCount());
//                }
//            }
//            System.out.println("\n\n");
//
//        }
    }

    /**
     * Starts Background Task for FeatureLocationManager.
     *
     * @param singleton
     * @see se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask
     */
    private void startBackgroundTask(HAnSSingleton singleton) {
        FeatureLocationBackgroundTask backgroundTask = new FeatureLocationBackgroundTask(singleton.getProject(),
                "Scanning progress",
                true,
                PerformInBackgroundOption.DEAF,
                singleton.getFeatureList());
        // TODO: singleton of ProgressIndicator
        ProgressIndicator empty = new EmptyProgressIndicator();

        ProgressManager progressManager = ProgressManager.getInstance();
        Logger.print("start background task");
        progressManager.runProcessWithProgressAsynchronously(backgroundTask, empty);
    }

    /**
     * test Class for Action performed. Use working code from actionPerformed
     * @see #actionPerformed(AnActionEvent) 
     * @param project
     * @return
     */
    /*private @NotNull Runnable testClassAction(Project project){

        FeatureLocationManager featureLocationManager = new FeatureLocationManager(project);

        for(var feature : FeatureModelUtil.findFeatures(project)){
            System.out.println("Checking: " + feature.getLPQText());
            var mapping = featureLocationManager.getFeatureFileMapping(feature);
            var map = mapping.getAllFeatureLocations();
            for(var path : map.keySet()){
                System.out.println("    File: " + path);
                for(var block : map.get(path)){
                    System.out.println("        Start: " + (block.getStartLine() + 1) + "\n        End: " + (block.getEndLine() + 1) + "\n        Total: " + block.getLineCount());
                }
            }
            System.out.println("\n\n");

        }
        return null;
    }
    void printFiles(PsiDirectory dir, String exclude, String ident){
        for(var file : dir.getFiles()){
            if(file.getName().equals(exclude))
                continue;
            System.out.println(ident + "> " + file.getName());
        }
        for(var subdir : dir.getSubdirectories()){
            System.out.println(ident + "/" + subdir.getName());
            printFiles(subdir, exclude, ident + "----");
        }
    }*/


}
