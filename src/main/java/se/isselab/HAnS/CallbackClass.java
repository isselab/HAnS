package se.isselab.HAnS;

import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import java.util.ArrayList;

public class CallbackClass implements HAnSCallback{
    @Override
    public void onComplete(ArrayList<FeatureFileMapping> featureFileMappings) {
        Logger.printWithTimestamp("Starting callback");
        for(var mapping : featureFileMappings){
            System.out.println(mapping.getParentFeature().getLPQText() + "  " + mapping.getTotalFeatureLineCount());
        }
        Logger.printWithTimestamp("Finished callback");
    }
}
