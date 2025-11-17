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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Immutable thread-safe structure holding all location information for a given feature within a project.
 * <p>
 * This class uses efficient data structures and algorithms to minimize memory footprint
 * and improve performance for large codebases:
 * <ul>
 *   <li>Immutable design - all state built in constructor via factory pattern</li>
 *   <li>SmartPsiElementPointer for GC-friendly PSI references</li>
 *   <li>Unmodifiable collections for thread safety</li>
 *   <li>LinkedHashMap for predictable iteration order and good performance</li>
 *   <li>Optimized line counting using BitSet for deduplication</li>
 * </ul>
 * </p>
 *
 * @implNote This class is fully immutable and thread-safe. All PSI access protected by ReadAction.
 */
public final class FeatureFileMapping {
    private static final Logger LOG = Logger.getInstance(FeatureFileMapping.class);

    public enum MarkerType {BEGIN, END, LINE, NONE}

    public enum AnnotationType {FOLDER, FILE, CODE}

    // Immutable state - all built in constructor
    private final Map<FileAnnotationKey, AnnotationData> locationMap;
    
    // Use SmartPsiElementPointer to prevent memory leaks - allows PSI tree to be garbage collected
    private final SmartPsiElementPointer<FeatureModelFeature> mappedFeaturePointer;

    /**
     * Private constructor - use factory method {@link #create(SmartPsiElementPointer, Map)} instead.
     * Ensures all state is built before instance creation (immutable pattern).
     */
    private FeatureFileMapping(@NotNull SmartPsiElementPointer<FeatureModelFeature> featurePointer,
                               @NotNull Map<FileAnnotationKey, AnnotationData> locationMap) {
        this.mappedFeaturePointer = featurePointer;
        // Create defensive immutable copy
        this.locationMap = Map.copyOf(locationMap);
    }

    /**
     * Factory method to create an immutable FeatureFileMapping from marker data.
     * <p>
     * This method processes all marker data efficiently by:
     * <ul>
     *   <li>Sorting markers once per file for O(n log n) performance</li>
     *   <li>Using a stack-based algorithm for matching BEGIN/END pairs</li>
     *   <li>Validating marker correctness and logging issues</li>
     *   <li>Building immutable structures from the start</li>
     * </ul>
     * </p>
     *
     * @param featurePointer SmartPsiElementPointer to the feature (prevents memory leaks)
     * @param markerData Map of file keys to marker data collected during scanning
     * @return Immutable FeatureFileMapping instance
     */
    @NotNull
    public static FeatureFileMapping create(
            @NotNull SmartPsiElementPointer<FeatureModelFeature> featurePointer,
            @NotNull Map<FileAnnotationKey, MarkerDataBuilder> markerData) {
        
        if (markerData.isEmpty()) {
            return new FeatureFileMapping(featurePointer, Collections.emptyMap());
        }

        // Build location map from marker data
        Map<FileAnnotationKey, AnnotationData> locationMap = new LinkedHashMap<>(markerData.size());
        
        for (Map.Entry<FileAnnotationKey, MarkerDataBuilder> entry : markerData.entrySet()) {
            FileAnnotationKey fileKey = entry.getKey();
            MarkerDataBuilder builder = entry.getValue();
            
            List<MarkerLine> markers = builder.markers();
            AnnotationType annotationType = builder.annotationType();
            
            // Sort markers by line number for efficient processing
            markers.sort(Comparator.comparingInt(MarkerLine::lineNumber));

            // Use stack for matching BEGIN/END pairs with appropriate initial capacity
            Deque<Integer> beginStack = new ArrayDeque<>(markers.size() / 2 + 1);
            
            // Pre-size block list
            List<FeatureLocationBlock> blocks = new ArrayList<>(markers.size());

            // Process each marker and build location blocks
            for (MarkerLine marker : markers) {
                switch (marker.type) {
                    case BEGIN -> beginStack.push(marker.lineNumber);
                    
                    case END -> {
                        if (beginStack.isEmpty()) {
                            LOG.warn(String.format(
                                "Found &end marker without matching &begin marker in [%s] at line [%d]. Metrics may be inaccurate.",
                                fileKey.filePath(), marker.lineNumber + 1
                            ));
                        } else {
                            int beginLine = beginStack.pop();
                            blocks.add(new FeatureLocationBlock(beginLine, marker.lineNumber));
                        }
                    }
                    
                    case LINE -> blocks.add(new FeatureLocationBlock(marker.lineNumber, marker.lineNumber));
                    
                    case NONE -> // File/folder annotations span entire file
                        blocks.add(new FeatureLocationBlock(0, marker.lineNumber));
                }
            }

            // Check for unmatched BEGIN markers
            validateBeginEndMatching(beginStack, fileKey);
            
            // Store immutable block list
            locationMap.put(fileKey, new AnnotationData(annotationType, List.copyOf(blocks)));
        }

        return new FeatureFileMapping(featurePointer, locationMap);
    }

    /**
     * Validates that all BEGIN markers have matching END markers.
     */
    private static void validateBeginEndMatching(@NotNull Deque<Integer> unmatchedBegins, 
                                                 @NotNull FileAnnotationKey fileKey) {
        if (!unmatchedBegins.isEmpty()) {
            for (Integer beginLine : unmatchedBegins) {
                LOG.warn(String.format(
                    "Missing closing &end marker for &begin in [%s] at line [%d]. Metrics may be inaccurate.",
                    fileKey.filePath(), beginLine + 1
                ));
            }
        }
    }

    /**
     * Gets the feature associated with this mapping.
     * <p>
     * IMPORTANT: This method performs PSI access and must be called within a ReadAction.
     * </p>
     *
     * @return The feature model feature, or null if PSI element has been garbage collected
     */
    @Nullable
    public FeatureModelFeature getFeature() {
        return ReadAction.compute(() -> mappedFeaturePointer.getElement());
    }

    /**
     * Gets the SmartPsiElementPointer for the feature (GC-safe reference).
     *
     * @return The smart pointer to the feature
     */
    @NotNull
    public SmartPsiElementPointer<FeatureModelFeature> getFeaturePointer() {
        return mappedFeaturePointer;
    }

    // &begin[FeatureLocation]

    /**
     * Gets all feature locations as an immutable list.
     * <p>
     * IMPORTANT: This method performs PSI access and must be called within a ReadAction.
     * </p>
     *
     * @return Immutable list of all feature locations
     */
    @NotNull
    public List<FeatureLocation> getFeatureLocations() {
        if (locationMap.isEmpty()) {
            return Collections.emptyList();
        }
        
        return ReadAction.compute(() -> {
            FeatureModelFeature feature = mappedFeaturePointer.getElement();
            if (feature == null) {
                LOG.warn("Feature PSI element has been garbage collected");
                return Collections.emptyList();
            }
            
            // Pre-size for better performance
            List<FeatureLocation> result = new ArrayList<>(locationMap.size());
            
            for (Map.Entry<FileAnnotationKey, AnnotationData> entry : locationMap.entrySet()) {
                FileAnnotationKey fileKey = entry.getKey();
                AnnotationData annotationData = entry.getValue();
                
                result.add(new FeatureLocation(
                    fileKey.filePath(),
                    fileKey.originatingAnnotationPath(),
                    feature,
                    annotationData.annotationType(),
                    annotationData.blocks()
                ));
            }
            
            return Collections.unmodifiableList(result);
        });
    }
    // &end[FeatureLocation]

    // &begin[FeatureLocation]

    /**
     * Gets feature location for a specific file efficiently.
     * <p>
     * IMPORTANT: This method performs PSI access and must be called within a ReadAction.
     * </p>
     *
     * @param fileKey File annotation key (path + originating annotation path)
     * @return Feature location for the file, or null if not found
     */
    @Nullable
    public FeatureLocation getFeatureLocationsForFile(@NotNull FileAnnotationKey fileKey) {
        AnnotationData annotationData = locationMap.get(fileKey);
        
        if (annotationData == null) {
            return null;
        }

        return ReadAction.compute(() -> {
            FeatureModelFeature feature = mappedFeaturePointer.getElement();
            if (feature == null) {
                LOG.warn("Feature PSI element has been garbage collected");
                return null;
            }
            
            return new FeatureLocation(
                fileKey.filePath(),
                fileKey.originatingAnnotationPath(),
                feature,
                annotationData.annotationType(),
                annotationData.blocks()
            );
        });
    }
    // &end[FeatureLocation]

    /**
     * Gets all unique file paths mapped to this feature.
     * <p>
     * Thread-safe, no ReadAction required (operates on immutable data).
     * </p>
     *
     * @return Immutable set of file paths
     */
    @NotNull
    public Set<String> getMappedFilePaths() {
        if (locationMap.isEmpty()) {
            return Collections.emptySet();
        }
        
        return locationMap.keySet().stream()
                .map(FileAnnotationKey::filePath)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Gets all file annotation keys (path + originating annotation).
     * <p>
     * Thread-safe, no ReadAction required (operates on immutable data).
     * </p>
     *
     * @return Immutable set of file annotation keys
     */
    @NotNull
    public Set<FileAnnotationKey> getMappedPathPair() {
        return Collections.unmodifiableSet(locationMap.keySet());
    }

    /**
     * Gets file/folder annotation mappings for a specific file path.
     * <p>
     * IMPORTANT: This method performs PSI access and must be called within a ReadAction.
     * </p>
     *
     * @param filePath The file path to filter by
     * @return Set of feature mappings for the file
     */
    @NotNull
    public Set<FeatureMappingInfo> getFilePathFeatureMappings(@NotNull String filePath) {
        if (locationMap.isEmpty()) {
            return Collections.emptySet();
        }
        
        return ReadAction.compute(() -> {
            FeatureModelFeature feature = mappedFeaturePointer.getElement();
            if (feature == null) {
                LOG.warn("Feature PSI element has been garbage collected");
                return Collections.emptySet();
            }
            
            String featureLpq = feature.getLPQText();
            
            return locationMap.entrySet().stream()
                    .filter(entry -> {
                        AnnotationType type = entry.getValue().annotationType();
                        String path = entry.getKey().filePath();
                        return (type == AnnotationType.FILE || type == AnnotationType.FOLDER) 
                                && path.equals(filePath);
                    })
                    .map(entry -> new FeatureMappingInfo(
                            featureLpq,
                            entry.getValue().annotationType().toString(),
                            entry.getKey().originatingAnnotationPath()
                    ))
                    .collect(Collectors.toUnmodifiableSet());
        });
    }

    // &begin[LineCount]

    /**
     * Gets the total line count for a feature in a specific file.
     * <p>
     * Uses {@link BitSet} for efficient line deduplication when blocks overlap.
     * This is significantly more memory-efficient than HashSet for dense line ranges.
     * </p>
     * <p>
     * Thread-safe, no ReadAction required (operates on immutable data).
     * </p>
     *
     * @param fileKey File annotation key (path + originating annotation path)
     * @return Number of unique lines covered by the feature
     */
    public int getFeatureLineCountInFile(@NotNull FileAnnotationKey fileKey) {
        AnnotationData annotationData = locationMap.get(fileKey);
        if (annotationData == null || annotationData.blocks().isEmpty()) {
            return 0;
        }

        List<FeatureLocationBlock> blocks = annotationData.blocks();
        
        // For single block, optimization: direct calculation
        if (blocks.size() == 1) {
            return blocks.getFirst().getLineCount();
        }
        
        // Find max line number to size BitSet appropriately
        int maxLine = blocks.stream()
                .mapToInt(FeatureLocationBlock::getEndLine)
                .max()
                .orElse(0);
        
        // BitSet is more memory-efficient than HashSet<Integer> for dense ranges
        BitSet lineSet = new BitSet(maxLine + 1);
        
        for (FeatureLocationBlock block : blocks) {
            lineSet.set(block.getStartLine(), block.getEndLine() + 1);
        }
        
        return lineSet.cardinality();
    }
    // &end[LineCount]

    // &begin[LineCount]

    /**
     * Gets the total line count across all files for this feature.
     * <p>
     * Thread-safe, no ReadAction required (operates on immutable data).
     * </p>
     *
     * @return Total number of unique feature lines across all files
     */
    public int getTotalFeatureLineCount() {
        if (locationMap.isEmpty()) {
            return 0;
        }
        
        return locationMap.keySet().stream()
                .mapToInt(this::getFeatureLineCountInFile)
                .sum();
    }
    // &end[LineCount]

    /**
     * Gets all folder annotations grouped by originating annotation file.
     * <p>
     * IMPORTANT: This method performs PSI access and must be called within a ReadAction.
     * </p>
     *
     * @return Immutable map of annotation file path to folder feature locations
     */
    @NotNull
    public Map<String, List<FeatureLocation>> getFolderAnnotations() {
        return getFeatureLocations().stream()
                .filter(fl -> fl.getAnnotationType() == AnnotationType.FOLDER)
                .collect(Collectors.groupingBy(
                    FeatureLocation::getMappedBy,
                    Collectors.toUnmodifiableList()
                ));
    }

    /**
     * Gets all file annotations grouped by originating annotation file.
     * <p>
     * IMPORTANT: This method performs PSI access and must be called within a ReadAction.
     * </p>
     *
     * @return Immutable map of annotation file path to file feature locations
     */
    @NotNull
    public Map<String, List<FeatureLocation>> getFileAnnotations() {
        return getFeatureLocations().stream()
                .filter(fl -> fl.getAnnotationType() == AnnotationType.FILE)
                .collect(Collectors.groupingBy(
                    FeatureLocation::getMappedBy,
                    Collectors.toUnmodifiableList()
                ));
    }

    /**
     * Semantic record for map keys - better than Pair<String, String>.
     * <p>
     * Using a record (Java 16+) provides:
     * <ul>
     *   <li>Immutability by default</li>
     *   <li>Clear semantic meaning (vs generic Pair)</li>
     *   <li>Compact memory layout</li>
     *   <li>Built-in equals/hashCode/toString</li>
     * </ul>
     * </p>
     */
    public record FileAnnotationKey(
            @NotNull String filePath,
            @NotNull String originatingAnnotationPath
    ) {}

    /**
     * Internal record for storing annotation data (type + blocks).
     * <p>
     * Immutable by default with efficient memory layout.
     * </p>
     */
    private record AnnotationData(
            @NotNull AnnotationType annotationType,
            @NotNull List<FeatureLocationBlock> blocks
    ) {}

    /**
     * Internal record for efficient marker data storage during build phase.
     * <p>
     * Immutable by default with compact memory layout.
     * </p>
     */
    private record MarkerLine(@NotNull MarkerType type, int lineNumber) {}

    /**
     * Builder for collecting marker data before creating immutable FeatureFileMapping.
     * <p>
     * This is used externally by FeatureLocationManager during the scanning phase.
     * </p>
     */
    public record MarkerDataBuilder(
            @NotNull AnnotationType annotationType,
            @NotNull List<MarkerLine> markers
    ) {
        public MarkerDataBuilder(@NotNull AnnotationType annotationType) {
            this(annotationType, new ArrayList<>(8));
        }

        public void addMarker(@NotNull MarkerType type, int lineNumber) {
            markers.add(new MarkerLine(type, lineNumber));
        }
    }

    /**
     * Semantic record for feature mapping information.
     * Better than nested Pair structures.
     */
    public record FeatureMappingInfo(
            @NotNull String featureLpq,
            @NotNull String annotationType,
            @NotNull String originPath
    ) {}
}
