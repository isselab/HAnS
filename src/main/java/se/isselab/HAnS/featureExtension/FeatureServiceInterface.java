package se.isselab.HAnS.featureExtension;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.List;

public interface FeatureServiceInterface {


    /**
     * Get Feature List from HAnS in Interface
     * @return
     */
    List<FeatureModelFeature> getFeatures();

    FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature);
    int getFeatureTangling(FeatureModelFeature feature);
    int getFeatureScattering(FeatureModelFeature feature);
    // convenience methods
    List<FeatureModelFeature> getChildFeatures(FeatureModelFeature feature);
    FeatureModelFeature getParentFeature(FeatureModelFeature feature);

    /**
     * Retrieve root/first-level-features from feature
     * @param feature
     * @return
     */
    FeatureModelFeature getRootFeature(FeatureModelFeature feature);

    // CRUD feature methods
    void createFeature(FeatureModelFeature feature);

    /**
     * Rename feature of Feature Model. Updated feature will be returned on success. On failure old feature will be returned.
     * @param feature
     * @return
     */
    FeatureModelFeature renameFeature(FeatureModelFeature feature);
    boolean deleteFeature(FeatureModelFeature feature);

    //TODO THESIS
    // add CRUD feature methods (important)
    // feature-scattering / tangling (important)
    // get feature-hierarchie (convenience)
    // get first-level-features or root (convenience)
}
