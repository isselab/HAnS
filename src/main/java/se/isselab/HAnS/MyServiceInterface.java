package se.isselab.HAnS;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Collection;
import java.util.List;

public interface MyServiceInterface {
    void doSomething(String param);

    List<FeatureModelFeature> getFeatures();

    FeatureFileMapping getFeatureFileMapping(FeatureModelFeature feature);

    //TODO THESIS
    // add CRUD feature methods (important)
    // feature-scattering / tangling (important)
    // get feature-hierarchie (convenience)
    // get first-level-features or root (convenience)
}
