package se.isselab.HAnS.metrics;

import com.intellij.openapi.project.Project;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Objects;

public class FeatureScattering {

    /**
     * Returns the scattering degree of the given feature while making use of a pre-calculated fileMapping
     * @param featureFileMapping fileMapping of the feature to search for
     * @return scattering degree of the given feature
     */
    public static int getScatteringDegree(FeatureFileMapping featureFileMapping){
        //TODO THESIS calculate scattering without intertwined locations
        int scatteringDegree = 0;

        for(var file : featureFileMapping.getMappedFilePaths()){
            var locations = featureFileMapping.getFeatureLocationsForFile(file);
            scatteringDegree += locations.getFeatureLocations().size();
        }

        return scatteringDegree;
    }
    /**
     * Returns the scattering degree of the given feature
     * @param feature feature to search for
     * @return scattering degree of the given feature
     */
    public static int getScatteringDegree(Project project, FeatureModelFeature feature){
        return getScatteringDegree(FeatureLocationManager.getFeatureFileMapping(project, feature));
    }
}
