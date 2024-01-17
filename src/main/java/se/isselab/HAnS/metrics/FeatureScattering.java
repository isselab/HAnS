package se.isselab.HAnS.metrics;

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
        int scatteringDegree = 0;

        for(var file : featureFileMapping.getAllFeatureLocations().keySet()){
            var locations = featureFileMapping.getAllFeatureLocations().get(file);
            scatteringDegree += locations.second.size();
        }

        return scatteringDegree;
    }
    /**
     * Returns the scattering degree of the given feature
     * @param feature feature to search for
     * @return scattering degree of the given feature
     */
    public static int getScatteringDegree(FeatureModelFeature feature){
        return getScatteringDegree(FeatureLocationManager.getFeatureFileMapping(feature));
    }
}
