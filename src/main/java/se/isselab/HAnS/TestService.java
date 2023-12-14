package se.isselab.HAnS;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service(Service.Level.PROJECT)
public final class TestService implements MyServiceInterface{
    private final Project project;

    public TestService(Project project){
        this.project = project;
    }

    @Override
    public void doSomething(String param){
        System.out.println("Received " + param);

        HashMap<String, ArrayList<FeatureLocationInfo>> map = FeatureLocationInfo.getAllFeatureLocations(project);
        for(String key : map.keySet()){
            System.out.println(key);
            for(FeatureLocationInfo info : map.get(key)){
                info.printMembers();
            }
        }
    }

    @Override
    public List<FeatureModelFeature> getFeatures() {
        return null;
    }

    @Override
    public FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature) {
        return null;
    }

}
