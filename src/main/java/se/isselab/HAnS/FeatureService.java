package se.isselab.HAnS;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationParameterImpl;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationLpqReferencesImpl;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.folderAnnotation.psi.impl.FolderAnnotationLpqImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service(Service.Level.PROJECT)
public final class FeatureService implements FeatureServiceInterface {
    private final Project project;

    public FeatureService(){
        this.project = ProjectManager.getInstance().getOpenProjects()[0];
    }

    /**
     * Get Feature List from HAnS in Service
     * @return
     */
    @Override
    public List<FeatureModelFeature> getFeatures() {
        return FeatureModelUtil.findFeatures(project);
    }

    @Override
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature) {
        return null;
    }

    @Override
    public int getFeatureTangling(FeatureModelFeature feature) {
        //TODO THESIS:
        // this approach does not take intertwined features into account
        // nor does it calculate tangling of different feature types ( folder which annotates a file which is annotated by a feature etc )

        //TODO THESIS:
        // check if metrics are correct
        //create a new map to save which and how often a feature is tangled with the specified feature
        HashMap<String, Integer> tanglingMap = new HashMap<>();
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        for(PsiReference reference : ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(project))){
            PsiElement element = reference.getElement();

            //determine file type and process content
            var fileType = element.getContainingFile();

            if(fileType instanceof CodeAnnotationFile){

                CodeAnnotationParameterImpl parentElement = PsiTreeUtil.getParentOfType(element, CodeAnnotationParameterImpl.class);
                var featureMarker = element.getParent().getParent();

                //skip endmarker to not count begin and end tangling as two separate entries
                if(featureMarker instanceof CodeAnnotationEndmarker)
                    continue;

                if(parentElement == null)
                    continue;
                for(var featureElement : parentElement.getLpqList()){
                    //compare if they are not the same and then increment degree of the pair
                    if(element != featureElement){
                        tanglingMap.merge(featureElement.getName(), 1, Integer::sum);
                    }
                }
            }
            else if (fileType instanceof FileAnnotationFile) {
                FileAnnotationLpqReferencesImpl parentElement = PsiTreeUtil.getParentOfType(element, FileAnnotationLpqReferencesImpl.class);
                if(parentElement == null)
                    continue;
                for(var featureElement : parentElement.getLpqList()){
                    //compare if they are not the same and then increment degree of the pair

                    if(element != featureElement)
                        tanglingMap.merge(featureElement.getName(), 1, Integer::sum);
                }
            }
            else if(fileType instanceof FolderAnnotationFile){
                var parentElement = element.getParent();
                if(parentElement instanceof FolderAnnotationFile)
                for(var featureElement : PsiTreeUtil.getChildrenOfType(parentElement, FolderAnnotationLpqImpl.class)){
                    if(element != featureElement){
                        tanglingMap.merge(featureElement.getName(), 1, Integer::sum);
                    }
                }

            }
        }
        int result = 0;
        for(var degree : tanglingMap.values()){
            result += degree;
        }
        return result;
    }

    @Override
    public int getFeatureScattering(FeatureModelFeature feature) {
        return 0;
    }

    @Override
    public List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature) {
        List<FeatureModelFeature> childs = new ArrayList<>();
        for(var child : feature.getChildren()) {
            childs.add((FeatureModelFeatureImpl)child);
        }
        return childs;
    }

    @Override
    public FeatureModelFeature getParentFeature(FeatureModelFeature feature) {
        if (feature.getParent() instanceof FeatureModelFile) {
            return (FeatureModelFeature) feature.getParent();
        }
        return (FeatureModelFeatureImpl) feature.getParent();
    }

    @Override
    public FeatureModelFeature getRootFeature(FeatureModelFeature feature) {
        FeatureModelFeature temp = feature;
        while(!(temp.getParent() instanceof FeatureModelFile)){
            temp = (FeatureModelFeature) temp.getParent();
        }
        return temp;
    }

    @Override
    public void createFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
    }

    @Override
    public FeatureModelFeature renameFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
        return null;
    }

    @Override
    public boolean deleteFeature(FeatureModelFeature feature) {
        // TODO: use existing function of HAnS
        return false;
    }

}
