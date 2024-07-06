/*
Copyright 2024 David Stechow & Philipp Kusmierz

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package se.isselab.HAnS.featureLocation;


import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Pair;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Structure which holds information of all locations inside the project for a given Feature.
 */
public class FeatureFileMapping {

    public enum MarkerType {BEGIN, END, LINE, NONE}

    public enum AnnotationType {FOLDER, FILE, CODE}

    private final HashMap<Pair<String,String>, Pair<AnnotationType, ArrayList<FeatureLocationBlock>>> map = new HashMap<>();
    private HashMap<Pair<String, String>, Pair<AnnotationType, ArrayList<Pair<MarkerType, Integer>>>> cache = new HashMap<>();
    private final FeatureModelFeature mappedFeature;

    public FeatureFileMapping(FeatureModelFeature feature) {
        mappedFeature = feature;
    }

    /**
     * Caches data for later processing via <code>buildFromQueue()</code>
     * This function should be called for each marker for a given feature before <code>buildFromQueue()</code> is called
     *
     * @param path           the file path which is mapped to the given line number
     * @param lineNumber     the line number within the specified file
     * @param type           the type of the feature marker
     * @param annotationType the annotation type - {file, folder, code}
     * @see #buildFromQueue()
     */
    public void enqueue(String path, int lineNumber, MarkerType type, AnnotationType annotationType, String originatingFilePath) {
        var key = new Pair<>(path, originatingFilePath);
        if (cache.get(key) != null) {
            if (cache.get(key).first != annotationType)
                // handle case when feature is annotated multiple times to same asset
                System.err.println("Feature is linked to file via different annotation types. This can result in inaccurate metrics. " + "[Feature: " + ReadAction.compute(mappedFeature::getLPQText) + "][File: " + path + "]");
            cache.get(key).second.add(new Pair<>(type, lineNumber));
        } else {

            ArrayList<Pair<MarkerType, Integer>> arr = new ArrayList<>();
            arr.add(new Pair<>(type, lineNumber));
            cache.put(key, new Pair<>(annotationType, arr));
        }
    }

    /**
     * Builds the cached data provided by <code>enqueue()</code> into corresponding featureLocationBlock-structures
     *
     * @see #enqueue(String, int, MarkerType, AnnotationType, String)
     */
    public void buildFromQueue() {
        if (cache == null || cache.isEmpty())
            return;

        for (var entry : cache.entrySet()) {        //building featureLocationBlocks from cache entries
            Deque<Integer> stack = new ArrayDeque<>();
            var key = entry.getKey();
            var annotationTypeToLocationBlockPair = entry.getValue();

            //sort in ascending order
            cache.get(key).second.sort(Comparator.comparing(p -> p.second));

            //create a featureLocationBlock for each (begin,end) or line
            for (var markerToLinePair : annotationTypeToLocationBlockPair.second) {

                switch (markerToLinePair.first) {
                    case BEGIN -> {
                        stack.push(markerToLinePair.second);
                    }
                    case END -> {
                        if (stack.isEmpty()) {
                            // found end marker without begin marker
                            System.err.printf("Found &end marker without matching &begin marker in [%s] at line [%d]. This will result in inaccurate metrics", key.first, markerToLinePair.second + 1);
                            continue;
                        }
                        int beginLine = stack.pop();
                        add(key, new FeatureLocationBlock(beginLine, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                    }
                    case LINE -> {
                        add(key, new FeatureLocationBlock(markerToLinePair.second, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                    }
                    case NONE -> {
                        // should only happen if file is a feature-to-file or feature-to-folder
                        add(key, new FeatureLocationBlock(0, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                        //System.out.println("[HAnS-Vis][ERROR] found marker of Type::None");
                    }
                    default -> {
                        // should not happen but cover case if no label was found
                    }
                }
            }
            if (!stack.isEmpty()) {
                // there was a begin without an endmarker
                for (var line : stack) {
                    // handle case when there was a begin marker without an end marker
                    String errorMessage = String.format("Missing closing &end marker for &begin in [%s] at line [%d].  This will result in inaccurate metrics", key.first, line + 1);
                    System.err.printf(errorMessage);
                }
            }
        }

        //clear cache and file
        cache = new HashMap<>();
    }

    /**
     * Method to get the Feature mapped to the FeatureFileMapping
     *
     * @return FeatureModelFeature mapped to the FeatureFileMapping
     */
    public FeatureModelFeature getFeature() {
        return mappedFeature;
    }


    /**
     * Maps the given file to a FeatureLocationBlock.
     *
     * @param pathPairOriginatingPath           the file pathPairOriginatingPath which is mapped to a given block
     * @param block          the location of the feature block inside the given file
     * @param annotationType the annotation type for the corresponding filepath
     */
    private void add(Pair<String,String> pathPairOriginatingPath, FeatureLocationBlock block, AnnotationType annotationType) {
        //check if file is already mapped to given feature

        //add block to already existing arraylist
        map.computeIfPresent(pathPairOriginatingPath, (k,v) -> {
            v.second.add(block);
            return v;
        });

        //add file and location to map
        ArrayList<FeatureLocationBlock> list = new ArrayList<>();
        list.add(block);
        map.put(pathPairOriginatingPath, new Pair<>(annotationType, list));
    }
    // &begin[FeatureLocation]

    /**
     * Method to get all FeatureLocations of the corresponding feature
     *
     * @return List of all FeatureLocations of the corresponding feature
     */
    public ArrayList<FeatureLocation> getFeatureLocations() {
        ArrayList<FeatureLocation> result = new ArrayList<>();
        for (var entry : map.entrySet()) {
            FeatureLocation location = new FeatureLocation(entry.getKey().first, entry.getKey().second, mappedFeature, entry.getValue().first, entry.getValue().second);
            result.add(location);
        }
        return result;
    }
    // &end[FeatureLocation]

    // &begin[FeatureLocation]

    /**
     * Method to get the FeatureLocations of a file for the corresponding feature
     *
     * @param filePathPair The File path paired with the origin of the annotation to retrieve the feature locations from
     * @return FeatureLocation structure which holds information on feature locations inside given path
     */
    public FeatureLocation getFeatureLocationsForFile(Pair<String, String> filePathPair) {
        if (!map.containsKey(filePathPair))
            return null;

        var entry = map.get(filePathPair);
        return new FeatureLocation(filePathPair.first, filePathPair.second, mappedFeature, entry.first, entry.second);
    }
    // &end[FeatureLocation]

    /**
     * Method to get a Set of all File paths tangled with the current feature
     *
     * @return Set<String></String> of all related Paths
     */
    public Set<String> getMappedFilePaths() {
        var keys = map.keySet();
        Set<String> result = new HashSet<>();
        for (var key : keys) {
            result.add(key.first);
        }
        return result;
    }

    public Set<Pair<String, String>> getMappedPathPair() {
        return map.keySet();
    }

    // &begin[LineCount]

    /**
     * Method to get the total line-count of a feature in a file specified by path
     *
     * @param pathPairOrigin path of the file which should be checked
     * @return line-count of a feature in the given file
     */
    public int getFeatureLineCountInFile(Pair<String, String> pathPairOrigin) {
        var annotationTypeToBlocksPair = map.get(pathPairOrigin);
        if (annotationTypeToBlocksPair == null)
            return 0;

        HashSet<Integer> lineSet = new HashSet<>();
        //add each individual line to the set
        for (FeatureLocationBlock block : annotationTypeToBlocksPair.second) {
            for (int i = block.getStartLine(); i <= block.getEndLine(); i++) {
                lineSet.add(i);
            }
        }
        return lineSet.size();
    }
    // &end[LineCount]

    // &begin[LineCount]

    /**
     * Method to get the total line-count of a feature for all files
     *
     * @return line-count of a feature
     */
    public int getTotalFeatureLineCount() {
        int total = 0;

        for (var key : map.keySet()) {
            total += getFeatureLineCountInFile(key);
        }

        return total;
    }
    // &end[LineCount]

    public Map<String, List<FeatureLocation>> getFolderAnnotations() {
        var featureLocations = getFeatureLocations();
        return featureLocations.stream().filter(fl -> fl.getAnnotationType() == AnnotationType.FOLDER)
                .collect(Collectors.groupingBy(FeatureLocation::getMappedBy));
    }

    public Map<String, List<FeatureLocation>> getFileAnnotations() {
        var featureLocations = getFeatureLocations();
        return featureLocations.stream().filter(fl -> fl.getAnnotationType() == AnnotationType.FILE)
                .collect(Collectors.groupingBy(FeatureLocation::getMappedBy));
    }
}
