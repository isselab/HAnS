package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;



import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;


import java.util.HashMap;

public class FeatureLocationManager {
    private HashMap<FeatureModelFeature, FeatureFileMapping> map = new HashMap<>();
    private final Project project;

    public FeatureLocationManager(Project project){
        this.project = project;
        //TODO THESIS
        // put initialization of features into corresponding facade / method
        for(var feature : FeatureModelUtil.findFeatures(project)){
            FeatureFileMapping featureFileMapping = new FeatureFileMapping(feature);

            //TODO THESIS
            // put reference search into background task
            for (PsiReference reference : ReferencesSearch.search(feature)) {
                //get comment sibling of the feature comment
                PsiElement element = reference.getElement();
                FeatureFileMapping.Type type;

                //TODO THESIS
                // check function (edge cases, return value etc)
                var commentElement = PsiTreeUtil.getContextOfType(reference.getElement(), PsiComment.class);
                if(commentElement == null) {
                    System.out.println("[ERROR] Reference was not a comment");
                    System.out.println("  " + reference.getElement().getContainingFile());
                    continue;
                }

                //TODO THESIS
                // use codeannotation language to get begin, end or line marker
                //get feature type
                if(commentElement.getText().contains("&begin"))
                    type = FeatureFileMapping.Type.begin;
                else if(commentElement.getText().contains("&end"))
                    type = FeatureFileMapping.Type.end;
                else if(commentElement.getText().contains("&line"))
                    type = FeatureFileMapping.Type.line;
                else
                    type = FeatureFileMapping.Type.none;



                //TODO THESIS
                // check .getVirtualFile for null exception which can occur in certain cases
                featureFileMapping.enqueue(element.getContainingFile().getVirtualFile().getPath(), getLine(commentElement, project), type);
            }
            featureFileMapping.buildFromQueue();
            map.put(feature, featureFileMapping);
        }
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
}
