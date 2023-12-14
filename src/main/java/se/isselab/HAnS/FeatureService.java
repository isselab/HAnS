package se.isselab.HAnS;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

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
        return 0;
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
