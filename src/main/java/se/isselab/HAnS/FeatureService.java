package se.isselab.HAnS;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.TestOnly;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationParameterImpl;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
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
import java.util.Objects;

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
                if(parentElement instanceof FolderAnnotationFile) {
                    for (var featureElement : Objects.requireNonNull(PsiTreeUtil.getChildrenOfType(parentElement, FolderAnnotationLpqImpl.class))) {
                        if (element != featureElement) {
                            tanglingMap.merge(featureElement.getName(), 1, Integer::sum);
                        }
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
        //TODO THESIS:
        // there can be multiple top level features - maybe return FeatureModelFeature[]
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

    @TestOnly
    public JSONObject getFeatureLineCountAsJson(){
        JSONArray featureLocationsJson = new JSONArray();
        for(var feature : FeatureModelUtil.findFeatures(project)) {

            var mapping = FeatureLocationManager.getFeatureFileMapping(feature);
            var map = mapping.getAllFeatureLocations();
            //get all lines of the given feature
            int totalLines = 0;
            for(var file : map.keySet()){
                totalLines += mapping.getFeatureLineCountInFile(file);
            }

            //convert total lines to size
            int minSize = 10;
            float size = Math.max(totalLines, minSize);

            //add options
            JSONObject attributes = new JSONObject();
            attributes.put("color", "#4f19c7");
            attributes.put("label", feature.getLPQText());
            attributes.put("attributes", new JSONArray());
            attributes.put("x", 0);
            attributes.put("y", 0);
            attributes.put("id", feature.getLPQText());
            attributes.put("size", size);

            featureLocationsJson.add(attributes);
        }
        JSONObject finalJson = new JSONObject();
        finalJson.put("nodes", featureLocationsJson);
        finalJson.put("edges", new JSONArray());

        /*for(feature : featureLocationsJson){
            JSONObject featureJson = new JSONObject();
            featureJson.put("color", "#4f19c7");
            featureJson.put("label", feature);
            featureJson.put("attributes", new JSONArray());
            featureJson.put("x", 0);
            featureJson.put("y", 0);
            featureJson.put("id", feature);

            int lineCount = feature


        }
         */
        return finalJson;
    }

    @TestOnly
    public JSONArray getFeatureModelAsJson(){
        var featureList = FeatureModelUtil.findFeatures(project);
        FeatureModelFeature root = getRootFeature(featureList.get(0));
        JSONArray data = new JSONArray();
        JSONObject rootObject = new JSONObject();

        var mapping = FeatureLocationManager.getFeatureFileMapping(root);
        rootObject.put("name", root.getLPQText());
        rootObject.put("value", getTotalLineCountWithChilds(root));
        rootObject.put("children", getChildFeaturesAsJson(root));
        data.add(rootObject);

        return data;
    }

    @TestOnly
    private JSONArray getChildFeaturesAsJson(FeatureModelFeature parentFeature) {
        JSONArray children = new JSONArray();
        var childFeatureList = getChildFeatures(parentFeature);

        //iterate over each child and recursively get its childs
        for(var child : childFeatureList){
            //get linecount of feature via mapping

            JSONObject childJson = new JSONObject();
            childJson.put("name", child.getLPQText());
            childJson.put("value", Math.max(getTotalLineCountWithChilds(child),1));
            childJson.put("children", getChildFeaturesAsJson(child));
            children.add(childJson);
        }
        return children;
    }

    @TestOnly
    private int getTotalLineCountWithChilds(FeatureModelFeature parent){
        int total = 1;
        for(var child : getChildFeatures(parent)){
            total += getTotalLineCountWithChilds(child);
        }
        total += FeatureLocationManager.getFeatureFileMapping(parent).getTotalFeatureLineCount();
        return total;
    }

}
