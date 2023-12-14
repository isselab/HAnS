package se.isselab.HAnS.featureLocation;


import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.*;

import static com.jediterm.terminal.util.Pair.getSecond;

public class FeatureFileMapping {
    public enum Type {begin, end, line, none}
    private HashMap<String, ArrayList<FeatureLocationBlock>> map = new HashMap<>();
    private HashMap<String, ArrayList<Pair<Type, Integer>>> cache = new HashMap<>();
    private final FeatureModelFeature parentFeature;

    public FeatureFileMapping(FeatureModelFeature feature){
        parentFeature = feature;
    }
    /**
     * Caches data for later processing via <code>buildFromQueue()</code>
     *
     * @param path the file path which is mapped to the given linenumber
     * @param lineNumber the linenumber within the specified file
     * @param type the type of the feature marker
     *
     * @see #buildFromQueue()
     */
    public void enqueue(String path, int lineNumber, Type type){
        if(cache.get(path) != null){
            cache.get(path).add(new Pair<>(type, lineNumber));
        }
        else{
            ArrayList<Pair<Type, Integer>> arr = new ArrayList<>();
            arr.add(new Pair<>(type, lineNumber));
            cache.put(path, arr);
        }
    }

    /**
     * Builds the cached data provided by <code>enqueue()</code> into corresponding featureLocationBlock-structures
     *
     * @see #enqueue(String, int, Type)
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
            cache.get(path).sort(Comparator.comparing(p -> p.second));

            //create a featureLocationBlock for each (begin,end) or line
            for (var pair : cache.get(path)) {
                switch (pair.first) {
                    case begin: {
                        stack.push(pair.second);
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
                        add(path, new FeatureLocationBlock(beginLine, pair.second));
                        break;
                    }

                    case line: {
                        add(path, new FeatureLocationBlock(pair.second, pair.second));
                        break;
                    }

                    case none: {
                        //TODO THESIS
                        // should not happen but cover case if "none" label was found
                        System.out.println("[HAnS-Vis][ERROR] found marker of Type::None");
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
     */
    public void add(String path, FeatureLocationBlock block){
        //check if file is already mapped to given feature

        //add block to already existing arraylist
        if(map.containsKey(path)){
            map.get(path).add(block);
            return;
        }

        //add file and location to map
        ArrayList<FeatureLocationBlock> list = new ArrayList<>();
        list.add(block);
        map.put(path,  list);
    }

    public ArrayList<FeatureLocationBlock> getFeatureLocationBlocks(PsiFile file){
        //TODO THESIS
        // returning new arraylist to prevent altering of private list - check whether it is suitable
        return new ArrayList<>(map.get(file));
    }

    //TODO THESIS
    // maybe remove access to complete hashmap
    public Map<String, List<FeatureLocationBlock>> getAllFeatureLocations(){
        //TODO THESIS
        // returning new map to prevent altering of private map - check whether it is suitable
        return new HashMap<>(map);
    }



}
