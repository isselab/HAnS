package se.isselab.HAnS.featureLocation;


import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import se.isselab.HAnS.Logger;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.*;

import static com.jediterm.terminal.util.Pair.getSecond;

/**
 * Object which contains mappings for filePaths
 * to its corresponding annotationType and a list of FeatureLocationBlocks
 */
public class FeatureFileMapping {
    public enum MarkerType {begin, end, line, none}
    public enum AnnotationType {folder, file, code}
    private HashMap<String, Pair<AnnotationType,ArrayList<FeatureLocationBlock>>> map = new HashMap<>();
    private HashMap<String, Pair<AnnotationType,ArrayList<Pair<MarkerType, Integer>>>> cache = new HashMap<>();
    private final FeatureModelFeature parentFeature;

    public FeatureFileMapping(FeatureModelFeature feature){
        parentFeature = feature;
    }
    /**
     * Caches data for later processing via <code>buildFromQueue()</code>
     * This function should be called for each marker for a given feature before <code>buildFromQueue()</code> is called
     *
     * @param path the file path which is mapped to the given linenumber
     * @param lineNumber the linenumber within the specified file
     * @param type the type of the feature marker
     * @param annotationType the annotation type - {file, folder, code}
     *
     * @see #buildFromQueue()
     */
    public void enqueue(String path, int lineNumber, MarkerType type, AnnotationType annotationType){
        if(cache.get(path) != null){
            if(cache.get(path).first != annotationType)
                Logger.print(Logger.Channel.WARNING, "Feature is linked to file via different annotation types. This can result in inaccurate metrics. " + "[Feature: " + parentFeature.getLPQText() + "][File: " + path + "]");
            cache.get(path).second.add(new Pair<>(type, lineNumber));
        }
        else{

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
    public void buildFromQueue(){
        //TODO THESIS
        // handle case if build from queue gets called on an empty queue
        if(cache == null || cache.isEmpty())
            return;

        for(String path : cache.keySet())
        {        //building featureLocationBlocks from cache entries
            Stack<Integer> stack = new Stack<>();
            //sort in ascending order
            cache.get(path).second.sort(Comparator.comparing(p -> p.second));

            var annotationTypeToLocationBlockPair = cache.get(path);
            //create a featureLocationBlock for each (begin,end) or line
            for (var markerToLinePair : annotationTypeToLocationBlockPair.second) {

                switch (markerToLinePair.first) {
                    case begin: {
                        stack.push(markerToLinePair.second);
                        break;
                    }
                    case end: {
                        if (stack.isEmpty()) {
                            //TODO THESIS
                            // error-handling if no matching begin was found for the end
                            System.out.println("[HAnS-Vis][ERROR] found end marker without matching begin marker");
                            continue;
                        }
                        int beginLine = stack.pop();
                        add(path, new FeatureLocationBlock(beginLine, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                        break;
                    }

                    case line: {
                        add(path, new FeatureLocationBlock(markerToLinePair.second, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                        break;
                    }

                    case none: {
                        //TODO THESIS
                        // should only happen if file is a feature-to-file or feature-to-folder
                        add(path, new FeatureLocationBlock(0, markerToLinePair.second), annotationTypeToLocationBlockPair.first);
                        //System.out.println("[HAnS-Vis][ERROR] found marker of Type::None");
                        break;
                    }

                    default: {
                        //TODO THESIS
                        // should not happen but cover case if no label was found
                        System.out.println("[HAnS-Vis][ERROR] found marker with no type");
                    }

                }
            }
            if (!stack.isEmpty()) {
                //TODO THESIS
                // there was a begin without an endmarker
                System.out.println("[HAnS-Vis][ERROR] missing closing end marker");
            }
        }
        //TODO THESIS
        // check if feature is intertwined and dont count lines twice
        // e.g begin[move, position] ... begin[position] ... end[position] ...end[position] ... end[move]

        //clear cache and file
        cache = new HashMap<>();
    }

    /**
     * Method to get the Feature mapped to the FeatureFileMapping
     * @return FeatureModelFeature mapped to the FeatureFileMapping
     */
    public FeatureModelFeature getParentFeature(){
        return parentFeature;
    }

    /**
     * Maps a whole file to the feature
     * @param path path to the file to be mapped
     */
    private void add(String path){
        //TODO THESIS
        // add path as whole feature

    }

    /**
     * Maps the given file to a block.
     *
     * @param path the file path which is mapped to a given block
     * @param block the location of the feature block inside the given file
     * @param annotationType the annotation type for the corresponding filepath
     */
    private void add(String path, FeatureLocationBlock block, AnnotationType annotationType){
        //check if file is already mapped to given feature

        //add block to already existing arraylist
        if(map.containsKey(path)){
            //TODO THESIS:
            // handle case if file is annotated multiple times - e.g Direction.java is mapped to feature "Move" via multiples of {feature-to-folder, feature-to-file or inline}

            map.get(path).second.add(block);
            return;
        }

        //add file and location to map
        ArrayList<FeatureLocationBlock> list = new ArrayList<>();
        list.add(block);
        map.put(path,  new Pair<>(annotationType, list));
    }

    public ArrayList<FeatureLocationBlock> getFeatureLocationBlocks(PsiFile file){
        //TODO THESIS
        // returning new arraylist to prevent altering of private list - check whether it is suitable
        return new ArrayList<>(map.get(file).second);
    }


    //TODO THESIS
    // maybe remove access to complete hashmap

    /**
     * Method to get a Map which contains mappings for filePaths (key)
     * to its corresponding annotationType (value.first) and a list of FeatureLocationBlocks (value.second)
     * @return HashMap with  path : (AnnotationType , LocationBlock[])
     */
    public Map<String, Pair<AnnotationType,ArrayList<FeatureLocationBlock>>> getAllFeatureLocations(){
        //TODO THESIS
        // returning new map to prevent altering of private map - check whether it is suitable
        return new HashMap<>(map);
    }



}
