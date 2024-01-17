package se.isselab.HAnS.featureExtension;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
// TODO: check whether all methods from FeatureService are also defined here
public interface FeatureServiceInterface {


    /**
     * Get Feature List from HAnS in Interface
     * @return
     */
    List<FeatureModelFeature> getFeatures();

    // &begin[FeatureFileMapping]
    FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature);
    void getFeatureFileMappingBackground(FeatureModelFeature feature, HAnSCallback callback);
    HashMap<String, FeatureFileMapping> getAllFeatureFileMappings();
    void getAllFeatureFileMappingsBackground(HAnSCallback callback);
    // &end[FeatureFileMapping]

    // &begin[Tangling]
    int getFeatureTangling(FeatureModelFeature feature);

    int getFeatureTangling(HashMap<String, FeatureFileMapping> fileMappings, FeatureModelFeature feature);

    void getFeatureTanglingBackground(FeatureModelFeature feature, HAnSCallback callback);

    HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap();
    HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(HashMap<String, FeatureFileMapping> featureFileMappings);
    // &end[Tangling]

    // &begin[Scattering]
    int getFeatureScattering(FeatureModelFeature feature);

    int getFeatureScattering(FeatureFileMapping featureFileMapping);

    void getFeatureScatteringBackground(FeatureModelFeature feature, HAnSCallback callback);

    // &end[Scattering]

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

    // &begin[Referencing]
    /**
     * Rename feature of Feature Model. Updated feature will be returned on success. On failure old feature will be returned.
     * @param feature
     * @return
     */
    FeatureModelFeature renameFeature(FeatureModelFeature feature, String newName);
    boolean deleteFeature(FeatureModelFeature feature);
    // &end[Referencing]
    void getFeatureMetricsBackground(HAnSCallback callback);

    //TODO THESIS
    // add CRUD feature methods (important)
    // feature-scattering / tangling (important)
    // get feature-hierarchie (convenience)
    // get first-level-features or root (convenience)
}
