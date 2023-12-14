package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import se.isselab.HAnS.FeatureAnnotationSearchScope;
import se.isselab.HAnS.Logger;
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
import se.isselab.HAnS.singleton.HAnSManager;


public class FeatureLocationManager implements HAnSObserverInterface {

    //TODO THESIS:
    // make sure intertwined featureblocks dont get counted multiple times
    private static final FeatureLocationManager featureLocationManager = new FeatureLocationManager();
    // TODO: getter für HAnSSingleton
    private FeatureLocationManager(){

    }

    public static FeatureLocationManager getInstance() {
        return featureLocationManager;
    }

    public static FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature){
        HAnSManager singleton = HAnSManager.getInstance();
        FeatureFileMapping featureFileMapping = new FeatureFileMapping(feature);
        //TODO THESIS
        // difference between using featureReference or featureReference.findAll
        Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(singleton.getProject()), true);

        for (PsiReference reference : featureReference) {
            //get comment sibling of the feature comment

            PsiElement element = reference.getElement();
            FeatureFileMapping.Type type;

            //determine file type and process content
            var fileType = element.getContainingFile();
            if(fileType instanceof CodeAnnotationFile){
                processCodeFile(featureFileMapping, element);
            }
            else if (fileType instanceof FileAnnotationFile) {
                processFeatureToFile(featureFileMapping, element);
            }
            else if(fileType instanceof FolderAnnotationFile){
                //TODO THESIS
                // implement
                System.out.println("Was a folder file");
            }

        }
        featureFileMapping.buildFromQueue();
        return featureFileMapping;
    }

    private static int getLine(PsiElement elem){
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(HAnSManager.getInstance().getProject());
        PsiFile openedFile = elem.getContainingFile();

        //iterate over each psiElement and check for PsiComment-Feature-Annotations
        if(openedFile == null)
            return -1;
        Document document = psiDocumentManager.getDocument(openedFile);
        if(document == null)
            return -1;

        return document.getLineNumber(elem.getTextRange().getStartOffset());
    }

    private static void processCodeFile(FeatureFileMapping featureFileMapping, PsiElement element){
        //TODO THESIS
        // check function (edge cases, return value etc)
        HAnSManager singleton = HAnSManager.getInstance();
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
        featureFileMapping.enqueue(element.getContainingFile().getVirtualFile().getPath(), getLine(commentElement), type);
    }

    private static void processFeatureToFile(FeatureFileMapping featureFileMapping, PsiElement element){
        //TODO THESIS
        // Get file reference instead of filename
        var parent = PsiTreeUtil.getParentOfType(element, FileAnnotationFileAnnotation.class);
        if(parent == null)
            return;


        var fileReferences = PsiTreeUtil.getChildrenOfType(parent, FileAnnotationFileReferences.class);
        if(fileReferences == null)
            return;

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
        /*
        Logger.print("FeatureLocationManager.onInit() call");
        HAnSManager singleton = HAnSManager.getInstance();
        List<FeatureModelFeature> featureList = FeatureModelUtil.findFeatures(singleton.getProject());
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
            i++;
        }

        //System.out.println("PRINTING JSON: " + a.toJSONString() + "\nEND OF JSON");

        singleton.notifyObservers(NotifyOption.UPDATE);
        */
    }
}
