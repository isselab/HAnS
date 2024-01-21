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


/**
 * Structure which holds information of all locations inside the project for a given Feature.
 */
public class FeatureFileMapping {
    public enum MarkerType {begin, end, line, none}

    public enum AnnotationType {folder, file, code}

    private final HashMap<String, Pair<AnnotationType, ArrayList<FeatureLocationBlock>>> map = new HashMap<>();
    private HashMap<String, Pair<AnnotationType, ArrayList<Pair<MarkerType, Integer>>>> cache = new HashMap<>();
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
    public void enqueue(String path, int lineNumber, MarkerType type, AnnotationType annotationType) {
        if (cache.get(path) != null) {
            if (cache.get(path).first != annotationType)
                // handle case when feature is annotated multiple times to same asset
                System.err.println("Feature is linked to file via different annotation types. This can result in inaccurate metrics. " + "[Feature: " + ReadAction.compute(mappedFeature::getLPQText) + "][File: " + path + "]");
            cache.get(path).second.add(new Pair<>(type, lineNumber));
        } else {

            ArrayList<Pair<MarkerType, Integer>> arr = new ArrayList<>();
            arr.add(new Pair<>(type, lineNumber));
            cache.put(path, new Pair<>(annotationType, arr));
        }
    }

    /**
     * Builds the cached data provided by <code>enqueue()</code> into corresponding featureLocationBlock-structures
     *
     * @see #enqueue(String, int, MarkerType, AnnotationType)
     */
    public void buildFromQueue() {
        if (cache == null || cache.isEmpty())
            return;

        for (String path : cache.keySet()) {        //building featureLocationBlocks from cache entries
            Stack<Integer> stack = new Stack<>();
            //sort in ascending order
            cache.get(path).second.sort(Comparator.comparing(p -> p.second));

            var annotationTypeToLocationBlockPair = cache.get(path);
            //create a featureLocationBlock for each (begin,end) or line
            for (var markerToLinePair : annotationTypeToLocationBlockPair.second) {

                switch (markerToLinePair.first) {
                    case begin -> {
                        stack.push(markerToLinePair.second);
                    }
                    case end -> {
                        if (stack.isEmpty()) {
                            // found end marker without begin marker
                            System.err.printf("Found &end marker without matching &begin marker in [%s] at line [%d]. This will result in inaccurate metrics", path, markerToLinePair.second + 1);
                            continue;
                        }
                        int beginLine = stack.pop();
                        add(path, new FeatureLocationBlock(beginLine, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                    }
                    case line -> {
                        add(path, new FeatureLocationBlock(markerToLinePair.second, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                    }
                    case none -> {
                        // should only happen if file is a feature-to-file or feature-to-folder
                        add(path, new FeatureLocationBlock(0, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
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
                    System.err.printf("Missing closing &end marker for &begin in [%s] at line [%d].  This will result in inaccurate metrics", path, line + 1);
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
     * @param path           the file path which is mapped to a given block
     * @param block          the location of the feature block inside the given file
     * @param annotationType the annotation type for the corresponding filepath
     */
    private void add(String path, FeatureLocationBlock block, AnnotationType annotationType) {
        //check if file is already mapped to given feature

        //add block to already existing arraylist
        if (map.containsKey(path)) {

            map.get(path).second.add(block);
            return;
        }

        //add file and location to map
        ArrayList<FeatureLocationBlock> list = new ArrayList<>();
        list.add(block);
        map.put(path, new Pair<>(annotationType, list));
    }
    // &begin[FeatureLocation]

    /**
     * Method to get all FeatureLocations of the corresponding feature
     *
     * @return List of all FeatureLocations of the corresponding feature
     */
    public ArrayList<FeatureLocation> getFeatureLocations() {
        ArrayList<FeatureLocation> result = new ArrayList<>();
        for (var filePath : map.keySet()) {
            var entry = map.get(filePath);
            FeatureLocation location = new FeatureLocation(filePath, mappedFeature, entry.first, entry.second);
            result.add(location);
        }
        return result;
    }
    // &end[FeatureLocation]

    // &begin[FeatureLocation]

    /**
     * Method to get the FeatureLocations of a file for the corresponding feature
     *
     * @param filePath The File path to retrieve the feature locations from
     * @return FeatureLocation structure which holds information on feature locations inside given path
     */
    public FeatureLocation getFeatureLocationsForFile(String filePath) {
        if (!map.containsKey(filePath))
            return null;

        var entry = map.get(filePath);
        return new FeatureLocation(filePath, mappedFeature, entry.first, entry.second);
    }
    // &end[FeatureLocation]

    /**
     * Method to get a Set of all File paths tangled with the current feature
     *
     * @return Set<String></String> of all related Paths
     */
    public Set<String> getMappedFilePaths() {
        return map.keySet();
    }

    // &begin[LineCount]

    /**
     * Method to get the total line-count of a feature in a file specified by path
     *
     * @param path path of the file which should be checked
     * @return line-count of a feature in the given file
     */
    public int getFeatureLineCountInFile(String path) {
        var annotationTypeToBlocksPair = map.get(path);
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

        for (var path : map.keySet()) {
            total += getFeatureLineCountInFile(path);
        }

        return total;
    }
    // &end[LineCount]


}
