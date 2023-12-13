package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import se.isselab.HAnS.Logger;
import se.isselab.HAnS.MyRunnable;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.featureExtension.HAnSObserverInterface;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;



import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileAnnotation;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReferences;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.singleton.HAnSSingleton;
import se.isselab.HAnS.singleton.NotifyOption;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class FeatureLocationManager implements HAnSObserverInterface {
    private HashMap<FeatureModelFeature, FeatureFileMapping> map = new HashMap<>();
    // private final Project project;

    public FeatureLocationManager(){
        /*this.project = project;
        //TODO THESIS
        // put initialization of features into corresponding facade / method






        FeatureLocationBackgroundTask backgroundTask = new FeatureLocationBackgroundTask(project,
                "Scanning progress",
                true,
                PerformInBackgroundOption.DEAF,
                featureList);


        ProgressIndicator empty = new EmptyProgressIndicator();



        ProgressManager progressManager = ProgressManager.getInstance();
        Logger.print("start background task");
        progressManager.runProcessWithProgressAsynchronously(backgroundTask, empty);

        Logger.print("vor Abruf");
        Collection<PsiReference> psiRef = backgroundTask.getPsiReference();
        Logger.print("nach Abruf");
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
            for (PsiReference reference : featureRefCollection) {
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
        HAnSSingleton singleton = HAnSSingleton.getHAnSSingleton();
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
        featureFileMapping.enqueue(element.getContainingFile().getVirtualFile().getPath(), getLine(commentElement, singleton.getProject()), type);
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

    @Override
    public void onUpdate() {

    }

    @Override
    public void onDelete() {

    }

    @Override
    public void onAdd() {

    }

    /**
     * Gets Locations of Features after {@link se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask} is done with ReferencesSearch.
     */
    @Override
    public void onInit() {
        Logger.print("FeatureLocationManager.onInit() call");
        HAnSSingleton singleton = HAnSSingleton.getHAnSSingleton();
        List<FeatureModelFeature> featureList = singleton.getFeatureList();
        List<Collection<PsiReference>> psiReferences = singleton.getPsiReferences();
        int i = 0;
        for(var feature : featureList) {

            FeatureFileMapping featureFileMapping = new FeatureFileMapping(feature);

            for (PsiReference reference : psiReferences.get(i)) {
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
            featureFileMapping.buildFromQueue();
            map.put(feature, featureFileMapping);
            i++;
        }
        singleton.setFeatureMapping(map);
        singleton.notifyObservers(NotifyOption.UPDATE);
    }
}
