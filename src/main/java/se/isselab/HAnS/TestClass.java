package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;

import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import io.netty.util.concurrent.CompleteFuture;
import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;


import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        FeatureLocationManager featureLocationManager = new FeatureLocationManager(e.getProject());

        for(var feature : FeatureModelUtil.findFeatures(e.getProject())){
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
    }


}
