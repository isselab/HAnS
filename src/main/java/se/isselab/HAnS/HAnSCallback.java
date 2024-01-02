package se.isselab.HAnS;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import java.util.ArrayList;

public interface HAnSCallback {
    void onComplete(ArrayList<FeatureFileMapping> featureFileMappings);
}
