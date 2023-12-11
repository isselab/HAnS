package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.FeatureAnnotationSearchScope;
import se.isselab.HAnS.MyRunnable;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;



import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileAnnotation;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReferences;


import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class FeatureLocationManager {
    private HashMap<FeatureModelFeature, FeatureFileMapping> map = new HashMap<>();
    private final Project project;

    public FeatureLocationManager(Project project){
        this.project = project;
        //TODO THESIS
        // put initialization of features into corresponding facade / method
        List<FeatureModelFeature> featureList = FeatureModelUtil.findFeatures(project);
        MyRunnable myRunnable = new MyRunnable(featureList, project);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(myRunnable,"Scan progress", false, project);
        System.out.println("vor Abruf");
        Collection<PsiReference> psiRef = myRunnable.getPsiReference();
        System.out.println("nach Abruf");
        /*
        for(var feature : FeatureModelUtil.findFeatures(project)) {

            FeatureFileMapping featureFileMapping = new FeatureFileMapping(feature);

            //TODO THESIS
            // put reference search into background task
            // replace for-loop with Coroutines or similar -> UI Freeze
            // ReferencesSearch.search(feature); TODO UI-Freeze: no problem

            Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(project), true);

            // testing purposes
            int time = LocalTime.now().toSecondOfDay();

            // ProgressManager.getInstance().runProcessWithProgressSynchronously(new MyRunnable(featureReference),"Scan progress", false, project);

            // testing purposes
            int timeDelta = LocalTime.now().toSecondOfDay() - time;

            System.out.println("Query done for feature " + feature.getFeatureName() + ": " + timeDelta + "s");
            /*for (PsiReference reference : featureRefCollection) {
                //get comment sibling of the feature comment

                PsiElement element = reference.getElement();
                System.out.println(element);
                FeatureFileMapping.Type type;

                //determine file type and process content
                var fileType = element.getContainingFile();
                if(fileType instanceof CodeAnnotationFile){
                    System.out.println("Code Annotation");
                   // processCodeFile(featureFileMapping, element);
                }
                else if (fileType instanceof FileAnnotationFile) {
                    System.out.println("Scanning feature-to-file file");
                    // processFeatureToFile(featureFileMapping, element);
                }
                else if(fileType instanceof FolderAnnotationFile){
                    System.out.println("Was a folder file");
                }


            }
            // featureFileMapping.buildFromQueue();
            // map.put(feature, featureFileMapping);
        }*/
    }


    public FeatureFileMapping getFeatureFileMapping(String lpq){
        for(var key : map.keySet()){
            if(key.getLPQText().equals(lpq))
                return map.get(key);
        }
        return null;
    }
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature){
        return map.get(feature);
    }

    public void add(FeatureModelFeature feature, FeatureFileMapping featureFileMapping){
        map.put(feature, featureFileMapping);
    }

    private int getLine(PsiElement elem, Project project){
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile openedFile = elem.getContainingFile();

        //iterate over each psiElement and check for PsiComment-Feature-Annotations
        if(openedFile == null)
            return -1;
        Document document = psiDocumentManager.getDocument(openedFile);
        if(document == null)
            return -1;

        return document.getLineNumber(elem.getTextRange().getStartOffset());
    }

    private void processCodeFile(FeatureFileMapping featureFileMapping, PsiElement element){
        //TODO THESIS
        // check function (edge cases, return value etc)
        var commentElement = PsiTreeUtil.getContextOfType(element, PsiComment.class);

        if(commentElement == null) {
            System.out.println("[ERROR] could not process comment");
            return;
        }

        var featureMarker = element.getParent().getParent();
        FeatureFileMapping.Type type;

        //get feature type
        if(featureMarker instanceof CodeAnnotationBeginmarker)
            type = FeatureFileMapping.Type.begin;
        else if(featureMarker instanceof CodeAnnotationEndmarker)
            type = FeatureFileMapping.Type.end;
        else if(featureMarker instanceof CodeAnnotationLinemarker)
            type = FeatureFileMapping.Type.line;
        else
            type = FeatureFileMapping.Type.none;

        //TODO THESIS
        // check .getVirtualFile for null exception which can occur in certain cases
        // get relative path to source
        featureFileMapping.enqueue(element.getContainingFile().getVirtualFile().getPath(), getLine(commentElement, project), type);
    }

    private void processFeatureToFile(FeatureFileMapping featureFileMapping, PsiElement element){
        //TODO THESIS
        // Get file reference instead of filename
        var parent = PsiTreeUtil.getParentOfType(element, FileAnnotationFileAnnotation.class);
        if(parent == null){
            return;
        }

        var fileReferences = PsiTreeUtil.getChildrenOfType(parent, FileAnnotationFileReferences.class);

        for(var ref : fileReferences){
            //get name of file
            for(var file : ref.getFileReferenceList()){
                String out = "";
                var child = file.getFirstChild();
                while(child != null){
                    if(child instanceof FileAnnotationFileName){
                        out += child.getText();
                        child = child.getNextSibling();
                    }
                }
                System.out.println("File: " + out);
            }
        }

    }
}
