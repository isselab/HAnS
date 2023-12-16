package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import se.isselab.HAnS.FeatureAnnotationSearchScope;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;



import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileAnnotation;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileName;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReferences;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.referencing.FileReferenceUtil;


public class FeatureLocationManager {

    //TODO THESIS:
    // make sure intertwined featureblocks dont get counted multiple times
    // TODO: getter für HAnSSingleton
    private FeatureLocationManager(){

    }

    public static FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature){
        FeatureFileMapping featureFileMapping = new FeatureFileMapping(feature);
        //TODO THESIS
        // how to get project
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(project), true);

        for (PsiReference reference : featureReference) {
            //get comment sibling of the feature comment

            PsiElement element = reference.getElement();
            FeatureFileMapping.MarkerType type;

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
        //TODO THESIS
        // how to get project
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

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

    private static void processCodeFile(FeatureFileMapping featureFileMapping, PsiElement element){
        //TODO THESIS
        // how to get project
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        //TODO THESIS
        // check function (edge cases, return value etc)
        var commentElement = PsiTreeUtil.getContextOfType(element, PsiComment.class);

        if(commentElement == null) {
            System.out.println("[ERROR] could not process comment");
            return;
        }

        var featureMarker = element.getParent().getParent();
        FeatureFileMapping.MarkerType type;

        //get feature type
        if(featureMarker instanceof CodeAnnotationBeginmarker)
            type = FeatureFileMapping.MarkerType.begin;
        else if(featureMarker instanceof CodeAnnotationEndmarker)
            type = FeatureFileMapping.MarkerType.end;
        else if(featureMarker instanceof CodeAnnotationLinemarker)
            type = FeatureFileMapping.MarkerType.line;
        else
            type = FeatureFileMapping.MarkerType.none;

        //TODO THESIS
        // check .getVirtualFile for null exception which can occur in certain cases
        // get relative path to source
        featureFileMapping.enqueue(element.getContainingFile().getOriginalFile().getVirtualFile().getPath(), getLine(commentElement), type, FeatureFileMapping.AnnotationType.code);

    }

    private static void processFeatureToFile(FeatureFileMapping featureFileMapping, PsiElement element){
        //TODO THESIS
        // how to get project
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

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

                //TODO THESIS get file of reference
                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);

                var psiFile = FileReferenceUtil.findFile(file, file.getFileName().getText()).get(0);

                Document document = psiDocumentManager.getDocument(psiFile);
                if(document == null)
                    return;

                //System.out.println("File: " + file.getFileName().getText());
                //TODO THESIS:
                // get relative path to source
                String[] temp = psiFile.getVirtualFile().getPath().split("/");
                String path = "/" + temp[temp.length - 1];

                featureFileMapping.enqueue(path, document.getLineCount() - 1, FeatureFileMapping.MarkerType.none, FeatureFileMapping.AnnotationType.file);
            }
        }
    }
}
