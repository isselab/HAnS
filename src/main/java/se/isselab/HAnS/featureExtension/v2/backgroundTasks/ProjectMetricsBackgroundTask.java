package se.isselab.HAnS.featureExtension.v2.backgroundTasks;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureExtension.v2.MetricsCallback;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureScattering;
import se.isselab.HAnS.metrics.v2.ProjectMetrics;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class ProjectMetricsBackgroundTask extends Task.Backgroundable {

    private ProjectMetrics metrics;
    private final MetricsCallback callback;

    /**
     * Background task needs a Callback class that implements IMetricsCallback.
     * It is necessary to callback after succeeding the background task
     *
     * @param project  current Project
     * @param title    Title for Progress Indicator
     * @param callback {@link MetricsCallback} Implementation
     * @param metrics  {@link ProjectMetrics} Implementation
     */
    public ProjectMetricsBackgroundTask(@Nullable Project project, @NotNull String title, MetricsCallback callback, ProjectMetrics metrics) {
        super(project, title);
        this.metrics = metrics;
        this.callback = callback;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {

        var featureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(super.getProject());
        var tanglingMap = getTanglingMap(super.getProject(), featureFileMappings);

        // Start the progress indicator
        indicator.setIndeterminate(false);
        indicator.setText("Getting metrics");
        indicator.setFraction(0.0);

        int total = featureFileMappings.values().size();
        int processed = 0;

        for(var featureFileMapping : featureFileMappings.values()){
            if (indicator.isCanceled()) break;

            var feature = featureFileMapping.getFeature();
            feature.setScatteringDegree(FeatureScattering.getScatteringDegree(featureFileMapping));
            var tanglingDegree = tanglingMap.containsKey(feature)? tanglingMap.get(feature).size() : 0;
            feature.setTanglingDegree(tanglingDegree);
            feature.setLineCount(featureFileMapping.getTotalFeatureLineCount());
            feature.setNumberOfAnnotatedFiles(featureFileMapping.getMappedFilePaths().size());
            feature.setNumberOfFolderAnnotations(featureFileMapping.getFolderAnnotations().size());
            feature.setNumberOfFileAnnotations(featureFileMapping.getFileAnnotations().size());

            // Update the progress
            processed++;
            indicator.setFraction((double) processed / total);
        }
        metrics = new ProjectMetrics(featureFileMappings, tanglingMap);

        // Finish the progress indicator
        indicator.setFraction(1.0);
        indicator.setText("Metrics loaded");
    }

    @Override
    public void onSuccess() {
        callback.onComplete(metrics);
    }

    /**
     * Returns a HashMap which is a 1:n feature mapping of feature to its tangled features while making use of a precalculated fileMapping
     *
     * @param fileMappings fileMappings which should be used
     * @return TanglingMap
     */
    public static HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> getTanglingMap(Project project, HashMap<String, FeatureFileMapping> fileMappings) {

        //map which contains Features and their tangled features
        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = new HashMap<>();

        //map which contains file to {features and their blocks}
        HashMap<String, HashMap<FeatureModelFeature, List<FeatureLocationBlock>>> featureFileMapping = new HashMap<>();
        //iterate over each feature and get the locations from them
        for (FeatureModelFeature feature : ReadAction.compute(() -> FeatureModelUtil.findFeatures(project))) {
            //get information for the corresponding feature
            var locationMap = fileMappings.get(ReadAction.compute(feature::getLPQText));

            //create entry for the featureFileMapping - this entry contains the feature and the feature locations within the file specified by filePath

            extractTangledFeatures(feature, locationMap, featureFileMapping, tanglingMap);
        }

        return tanglingMap;
    }

    private static void extractTangledFeatures(FeatureModelFeature feature, FeatureFileMapping locationMap, HashMap<String, HashMap<FeatureModelFeature, List<FeatureLocationBlock>>> featureFileMapping, HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap) {
        //iterate over each file inside this map
        for (var featureLocation : locationMap.getFeatureLocations()) {
            //get the path and the corresponding feature locations within this path
            String filePath = featureLocation.getMappedPath();
            List<FeatureLocationBlock> locations = featureLocation.getFeatureLocations();

            //add the {feature to location[]} to the fileMap
            var map = featureFileMapping.get(filePath);
            if (map != null) {
                //the file is already associated with features - check for tangling
                calculateTangledFeatureMap(feature, map, locations, tanglingMap);

            } else {
                //the file is new so we add a new entry
                HashMap<FeatureModelFeature, List<FeatureLocationBlock>> featureLocationMap = new HashMap<>();
                featureLocationMap.put(feature, locations);
                featureFileMapping.put(filePath, featureLocationMap);
            }
        }
    }

    private static void calculateTangledFeatureMap(FeatureModelFeature feature, HashMap<FeatureModelFeature, List<FeatureLocationBlock>> map, List<FeatureLocationBlock> locations, HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap) {
        for (var existingFeatureLocations : map.entrySet()) {
            //iterate over the locations of the feature and check if any of them do intersect
            for (FeatureLocationBlock block : locations) {
                if (block.hasSharedLines(existingFeatureLocations.getValue().toArray(new FeatureLocationBlock[0]))) {
                    //features share the same lines of code

                    //add tangling entry for both features a->b and b->a
                    var featureB = existingFeatureLocations.getKey();

                    //add featureB to featureA
                    if (tanglingMap.containsKey(feature)) {
                        tanglingMap.get(feature).add(featureB);
                    } else {
                        HashSet<FeatureModelFeature> featureSet = new HashSet<>();
                        featureSet.add(featureB);
                        tanglingMap.put(feature, featureSet);
                    }

                    //add featureA to featureB
                    if (tanglingMap.containsKey(featureB)) {
                        tanglingMap.get(featureB).add(feature);
                    } else {
                        HashSet<FeatureModelFeature> featureSet = new HashSet<>();
                        featureSet.add(feature);
                        tanglingMap.put(featureB, featureSet);
                    }
                }
            }
        }
        //add feature to the map
        map.put(feature, locations);
    }
}
