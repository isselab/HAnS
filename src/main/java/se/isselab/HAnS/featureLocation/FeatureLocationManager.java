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
import com.intellij.util.containers.ContainerUtil;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.isselab.HAnS.FeatureAnnotationSearchScope;
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.*;
import se.isselab.HAnS.featureAnnotation.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.featureAnnotation.fileAnnotation.psi.FileAnnotationFileAnnotation;
import se.isselab.HAnS.featureAnnotation.fileAnnotation.psi.FileAnnotationFileReferences;
import se.isselab.HAnS.featureAnnotation.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.featureLocation.FeatureFileMapping.FileAnnotationKey;
import se.isselab.HAnS.featureLocation.FeatureFileMapping.MarkerDataBuilder;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.referencing.FileReferenceUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe manager for feature location mappings with optimized caching and batch processing.
 * <p>
 * This class provides efficient access to feature-to-file mappings by:
 * <ul>
 *   <li>Using concurrent data structures for thread-safe operations</li>
 *   <li>Batching ReadAction calls to minimize overhead</li>
 *   <li>Implementing lazy initialization with proper cache invalidation</li>
 *   <li>Optimizing data structures for large codebases (1000+ files, 100+ features)</li>
 *   <li>Using SmartPsiElementPointer to prevent memory leaks</li>
 *   <li>Iterative directory traversal to prevent stack overflow</li>
 * </ul>
 * </p>
 *
 * @implNote All public methods are thread-safe. Cache invalidation occurs on project structure changes.
 */
public class FeatureLocationManager {

    private static final Logger log = LoggerFactory.getLogger(FeatureLocationManager.class);

    // Thread-safe cache with optimized concurrent access patterns
    private static final ConcurrentMap<Project, ConcurrentMap<String, FeatureFileMapping>> PROJECT_CACHES =
            ContainerUtil.createConcurrentWeakMap();

    // Track initialization state per project to support incremental loading
    private static final ConcurrentMap<Project, Set<String>> INITIALIZED_FEATURES =
            ContainerUtil.createConcurrentWeakMap();

    // Track full initialization per project
    private static final ConcurrentMap<Project, Boolean> FULLY_INITIALIZED =
            ContainerUtil.createConcurrentWeakMap();

    private FeatureLocationManager() {
        // Utility class - prevent instantiation
    }

    /**
     * Retrieves all feature file mappings for a given project.
     * <p>
     * This method performs lazy initialization - mappings are calculated on first access.
     * For better UI responsiveness, consider using this method within a background task.
     * </p>
     *
     * @param project The project to analyze
     * @return Immutable map of feature LPQ to corresponding file mapping
     * @see com.intellij.openapi.progress.Task.Backgroundable
     */
    public static Map<String, FeatureFileMapping> getAllFeatureFileMappings(@Nullable Project project) {
        if (project == null || project.isDisposed()) {
            log.warn("Attempted to get feature mappings for disposed project");
            return Collections.emptyMap();
        }

        if (!isFullyInitialized(project)) {
            calculateAllFeatureFileMappings(project);
        }

        return Collections.unmodifiableMap(getProjectCache(project));
    }

    // &begin[FeatureFileMapping]

    /**
     * Returns the feature file mapping for a specific feature.
     * <p>
     * This method uses lazy initialization - only the requested feature is calculated
     * if not already cached. For UI operations, wrap this call in a background task.
     * </p>
     *
     * @param project The project context
     * @param feature The feature to locate
     * @return Feature file mapping, or null if feature is invalid
     * @see com.intellij.openapi.progress.Task.Backgroundable
     */
    @Nullable
    public static FeatureFileMapping getFeatureFileMapping(@Nullable Project project, @NotNull FeatureModelFeature feature) {
        if (project == null || project.isDisposed()) {
            log.warn("Attempted to get feature mapping for disposed project");
            return null;
        }

        // Check validity and get LPQ in a single ReadAction
        String featureLpq = ReadAction.compute(() -> {
            if (!feature.isValid()) {
                return null;
            }
            return feature.getLPQText();
        });
        if (featureLpq == null) {
            return null;
        }

        // Check cache first
        ConcurrentMap<String, FeatureFileMapping> cache = getProjectCache(project);
        FeatureFileMapping cached = cache.get(featureLpq);
        if (cached != null) {
            return cached;
        }

        // Calculate if not initialized
        if (!isFeatureInitialized(project, featureLpq) && !isFullyInitialized(project)) {
            calculateFeatureFileMapping(project, feature);
            return cache.get(featureLpq);
        }

        return null;
    }

    /**
     * Calculates all feature file mappings for a project using batch processing.
     * <p>
     * This method optimizes performance by:
     * <ul>
     *   <li>Batch reading all features in a single ReadAction</li>
     *   <li>Using SmartPsiElementPointer to prevent memory leaks</li>
     *   <li>Building immutable FeatureFileMapping instances</li>
     * </ul>
     * </p>
     *
     * @param project The project to analyze
     */
    public static void calculateAllFeatureFileMappings(@NotNull Project project) {
        if (project.isDisposed()) {
            return;
        }

        long startTime = System.currentTimeMillis();
        log.debug("Starting calculation of all feature file mappings for project: {}", project.getName());

        // Batch read all features in a single ReadAction to minimize overhead
        List<FeatureModelFeature> features = ReadAction.compute(() -> {
            if (project.isDisposed()) {
                return Collections.emptyList();
            }
            return new ArrayList<>(FeatureModelUtil.findFeatures(project));
        });

        if (features.isEmpty()) {
            log.debug("No features found in project");
            setFullyInitialized(project, true);
            return;
        }

        log.debug("Found {} features to process", features.size());

        // Process each feature - could be parallelized for very large projects
        for (FeatureModelFeature feature : features) {
            if (project.isDisposed()) {
                continue;
            }
            // Check validity in ReadAction
            boolean isValid = ReadAction.compute(feature::isValid);
            if (!isValid) {
                continue;
            }
            calculateFeatureFileMapping(project, feature);
        }

        setFullyInitialized(project, true);

        long duration = System.currentTimeMillis() - startTime;
        log.debug("Completed calculation of {} feature file mappings in {}ms", features.size(), duration);
    }

    /**
     * Calculates the file mapping for a single feature with optimized read operations.
     * <p>
     * This method batches multiple PSI queries into fewer ReadAction calls to improve performance.
     * Uses SmartPsiElementPointer to prevent memory leaks from PSI references.
     * </p>
     *
     * @param project The project context
     * @param feature The feature to analyze
     */
    public static void calculateFeatureFileMapping(@NotNull Project project, @NotNull FeatureModelFeature feature) {
        if (project.isDisposed()) {
            return;
        }

        // Check validity and get LPQ in a single ReadAction
        String featureLpq = ReadAction.compute(() -> {
            if (!feature.isValid()) {
                return null;
            }
            return feature.getLPQText();
        });
        if (featureLpq == null) {
            log.warn("Feature has null LPQ text, skipping");
            return;
        }

        long startTime = System.currentTimeMillis();
        log.debug("Calculating feature file mapping for: {}", featureLpq);

        // Create SmartPsiElementPointer to prevent memory leaks
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = ReadAction.compute(() ->
                SmartPointerManager.getInstance(project).createSmartPsiElementPointer(feature)
        );

        // Build marker data map (mutable during construction)
        Map<FileAnnotationKey, MarkerDataBuilder> markerDataMap = new HashMap<>();

        // Execute reference search in smart mode (when indices are ready) - this is the expensive operation
        // This prevents IndexNotReadyException during indexing
        List<ReferenceData> referenceDataList = DumbService.getInstance(project).tryRunReadActionInSmartMode(() -> {
            if (project.isDisposed()) {
                return Collections.emptyList();
            }

            // Execute reference search
            Query<PsiReference> featureReference = ReferencesSearch.search(
                    feature,
                    FeatureAnnotationSearchScope.projectScope(project),
                    true
            );

            // Collect reference data
            List<ReferenceData> dataList = new ArrayList<>();
            for (PsiReference reference : featureReference) {
                PsiElement element = reference.getElement();
                if (!element.isValid()) {
                    continue;
                }

                PsiFile containingFile = element.getContainingFile();
                if (containingFile != null) {
                
                    var virtualFile = containingFile.getVirtualFile();
                    if (virtualFile == null) {
                        continue;
                    }

                    dataList.add(new ReferenceData(
                            element,
                            containingFile,
                            virtualFile.getPath()
                    ));
                }
            }
            return dataList;
        }, "Feature Location Reference Search");

        if (referenceDataList == null)
        {
            log.warn("Reference search returned null for feature: {}", featureLpq);
            return;
        }
        // Process each reference type
        for (ReferenceData refData : referenceDataList) {
            if (project.isDisposed()) {
                break;
            }

            ReadAction.run(() -> {
                if (!refData.element.isValid() || !refData.containingFile.isValid()) {
                    return;
                }

                // Handle code annotations - these can be injected into any file with comments
                if (refData.containingFile instanceof CodeAnnotationFile) {
                    processCodeFile(project, markerDataMap, refData.element, refData.originatingFilePath, featureLpq);
                } else if (refData.containingFile instanceof FileAnnotationFile) {
                    processFeatureToFile(project, markerDataMap, refData.element, refData.originatingFilePath);
                } else if (refData.containingFile instanceof FolderAnnotationFile) {
                    PsiDirectory dir = refData.containingFile.getContainingDirectory();
                    if (dir != null) {
                        processFeatureToFolder(project, markerDataMap, dir, refData.originatingFilePath);
                    }
                }
            });
        }

        FeatureFileMapping featureFileMapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        // Update caches atomically
        getProjectCache(project).put(featureLpq, featureFileMapping);
        markFeatureInitialized(project, featureLpq);

        long duration = System.currentTimeMillis() - startTime;
        log.debug("Completed file mapping for feature [{}] in {}ms", featureLpq, duration);
    }

    // &end[FeatureFileMapping]

    /**
     * Processes code annotation references with batched PSI access.
     * <p>
     * All PSI access is already protected by ReadAction in calling method.
     * Handles both regular Java files and injected annotations in XML/other files.
     * </p>
     */
    private static void processCodeFile(@NotNull Project project,
                                        @NotNull Map<FileAnnotationKey, MarkerDataBuilder> markerDataMap,
                                        @NotNull PsiElement element,
                                        @NotNull String originatingFilePath,
                                        @NotNull String featureLpq) {
        if (!element.isValid()) {
            return;
        }

        // Get the injection host (the actual comment in the host file, e.g., XML comment)
        // This is needed because element might be in an injected CodeAnnotationFile
        InjectedLanguageManager injManager = InjectedLanguageManager.getInstance(project);
        PsiLanguageInjectionHost injectionHost = injManager.getInjectionHost(element);

        PsiComment commentElement;
        if (injectionHost instanceof PsiComment) {
            // Injected annotation (e.g., in XML comment) - use the host comment
            commentElement = (PsiComment) injectionHost;
        } else {
            // Regular annotation in Java file - find comment in PSI tree
            commentElement = PsiTreeUtil.getContextOfType(element, PsiComment.class);
        }

        if (commentElement == null) {
            return;
        }

        PsiElement featureMarker = element.getParent().getParent();
        FeatureFileMapping.MarkerType type = getMarkerType(featureMarker);

        // Get the host file (e.g., plugin.xml for injected annotations, or .java for regular annotations)
        var file = commentElement.getContainingFile().getVirtualFile();
        if (file == null) {
            return;
        }

        int lineNumber = getLine(project, commentElement);
        if (lineNumber < 0) {
            return;
        }

        FileAnnotationKey key = new FileAnnotationKey(file.getPath(), originatingFilePath);
        MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key,
                k -> new MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));

        // Validate annotation type consistency
        if (builder.annotationType() != FeatureFileMapping.AnnotationType.CODE) {
            log.warn("Feature [{}] is linked to file [{}] via different annotation types. This may result in inaccurate metrics.", featureLpq, file.getPath());
        }

        builder.addMarker(type, lineNumber);
    }

    /**
     * Determines the marker type from a PSI element using pattern matching.
     *
     * @param featureMarker The PSI element to check
     * @return The marker type, or NONE if not recognized
     */
    @NotNull
    private static FeatureFileMapping.MarkerType getMarkerType(@Nullable PsiElement featureMarker) {
        // Use instanceof pattern matching (Java 16+) for cleaner code
        return switch (featureMarker) {
            case CodeAnnotationBeginmarker ignored -> FeatureFileMapping.MarkerType.BEGIN;
            case CodeAnnotationEndmarker ignored -> FeatureFileMapping.MarkerType.END;
            case CodeAnnotationLinemarker ignored -> FeatureFileMapping.MarkerType.LINE;
            case null, default -> FeatureFileMapping.MarkerType.NONE;
        };
    }

    /**
     * Processes file annotation references efficiently.
     * <p>
     * All PSI access is already protected by ReadAction in calling method.
     * </p>
     */
    private static void processFeatureToFile(@NotNull Project project,
                                             @NotNull Map<FileAnnotationKey, MarkerDataBuilder> markerDataMap,
                                             @NotNull PsiElement element,
                                             @NotNull String originatingFilePath) {
        if (!element.isValid()) {
            return;
        }

        FileAnnotationFileAnnotation parent = PsiTreeUtil.getParentOfType(element, FileAnnotationFileAnnotation.class);
        if (parent == null) {
            return;
        }

        FileAnnotationFileReferences[] fileReferences = PsiTreeUtil.getChildrenOfType(parent, FileAnnotationFileReferences.class);
        if (fileReferences == null || fileReferences.length == 0) {
            return;
        }

        enqueueFileReferences(project, markerDataMap, fileReferences, originatingFilePath);
    }

    /**
     * Enqueues file references with optimized document access.
     * <p>
     * All PSI access is already protected by ReadAction in calling method.
     * </p>
     */
    private static void enqueueFileReferences(@NotNull Project project,
                                              @NotNull Map<FileAnnotationKey, MarkerDataBuilder> markerDataMap,
                                              @NotNull FileAnnotationFileReferences[] fileReferences,
                                              @NotNull String originatingFilePath) {
        for (FileAnnotationFileReferences ref : fileReferences) {
            for (var fileRef : ref.getFileReferenceList()) {
                if (!fileRef.isValid()) {
                    continue;
                }

                String fileName = fileRef.getFileName().getText();
                List<PsiFile> foundFiles = FileReferenceUtil.findFile(fileRef, fileName);

                if (foundFiles.isEmpty()) {
                    continue;
                }

                PsiFile psiFile = foundFiles.getFirst();
                if (psiFile == null || !psiFile.isValid()) {
                    continue;
                }

                int lastLine = getLine(project, psiFile);

                var key = getFileAnnotationKey(psiFile, originatingFilePath);
                MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key,
                        k -> new MarkerDataBuilder(FeatureFileMapping.AnnotationType.FILE));

                builder.addMarker(FeatureFileMapping.MarkerType.NONE, lastLine);
            }
        }
    }

    /**
     * Processes folder annotations with ITERATIVE directory traversal to prevent stack overflow.
     * <p>
     * FIXED: Replaced recursive traversal with iterative approach using Deque.
     * This prevents StackOverflowError on deep directory trees.
     * </p>
     * <p>
     * All PSI access is already protected by ReadAction in calling method.
     * </p>
     */
    private static void processFeatureToFolder(@NotNull Project project,
                                               @NotNull Map<FileAnnotationKey, MarkerDataBuilder> markerDataMap,
                                               @NotNull PsiDirectory rootDirectory,
                                               @NotNull String originatingFilePath) {
        if (!rootDirectory.isValid()) {
            return;
        }

        // Use Deque for iterative traversal - prevents stack overflow
        Deque<PsiDirectory> directoryStack = new ArrayDeque<>();
        directoryStack.push(rootDirectory);

        while (!directoryStack.isEmpty()) {
            PsiDirectory currentDir = directoryStack.pop();

            if (!currentDir.isValid()) {
                continue;
            }

            List<PsiFile> filteredFiles = Arrays.stream(currentDir.getFiles())
                    .filter(file -> !(file instanceof FolderAnnotationFile
                            || file instanceof FileAnnotationFile))
                    .toList();

            // Process files in current directory
            for (PsiFile file :filteredFiles) {
                if (!file.isValid()) {
                    continue;
                }

                int lastLine = getLastLine(project, file);
                var key = getFileAnnotationKey(file, originatingFilePath);
                if (key == null || lastLine == -1) {
                    continue;
                }
                MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key,
                        k -> new MarkerDataBuilder(FeatureFileMapping.AnnotationType.FOLDER));

                builder.addMarker(FeatureFileMapping.MarkerType.NONE, lastLine);
            }

            // Add subdirectories to stack (iterative, not recursive)
            for (PsiDirectory subdirectory : currentDir.getSubdirectories()) {
                if (subdirectory.isValid()) {
                    directoryStack.push(subdirectory);
                }
            }
        }
    }

    private static int getLastLine(@NotNull Project project, @NotNull PsiFile file) {
        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(file);
        if (document == null) {
            return -1;
        }
        return document.getLineCount() > 0 ? document.getLineCount() - 1 : 0;
    }

    private static FileAnnotationKey getFileAnnotationKey(PsiFile file, String originatingFilePath) {
        var virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return null;
        }

        return new FileAnnotationKey(virtualFile.getPath(), originatingFilePath);
    }

    /**
     * Efficiently computes the line number for a PSI element.
     * <p>
     * All PSI access is already protected by ReadAction in calling method.
     * </p>
     */
    private static int getLine(@NotNull Project project, @NotNull PsiElement elem) {
        if (!elem.isValid()) {
            return -1;
        }

        PsiFile containingFile = elem.getContainingFile();
        if (containingFile == null) {
            return -1;
        }

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        Document document = psiDocumentManager.getDocument(containingFile);
        if (document == null) {
            return -1;
        }

        return document.getLineNumber(elem.getTextRange().getStartOffset());
    }

    /**
     * Invalidates cached mappings for a project.
     * Should be called when project structure changes significantly.
     *
     * @param project The project to invalidate cache for
     */
    public static void invalidateCache(@NotNull Project project) {
        log.debug("Invalidating feature location cache for project: {}", project.getName());
        PROJECT_CACHES.remove(project);
        INITIALIZED_FEATURES.remove(project);
        FULLY_INITIALIZED.remove(project);
    }

    /**
     * Invalidates cache for a specific feature.
     *
     * @param project    The project context
     * @param featureLpq The feature LPQ to invalidate
     */
    public static void invalidateFeature(@NotNull Project project, @NotNull String featureLpq) {
        log.debug("Invalidating cache for feature: {}", featureLpq);
        getProjectCache(project).remove(featureLpq);
        Set<String> initialized = INITIALIZED_FEATURES.get(project);
        if (initialized != null) {
            initialized.remove(featureLpq);
        }
    }

    // Cache management helpers

    @NotNull
    private static ConcurrentMap<String, FeatureFileMapping> getProjectCache(@NotNull Project project) {
        return PROJECT_CACHES.computeIfAbsent(project,
                p -> new ConcurrentHashMap<>());
    }

    private static boolean isFeatureInitialized(@NotNull Project project, @NotNull String featureLpq) {
        Set<String> initialized = INITIALIZED_FEATURES.get(project);
        return initialized != null && initialized.contains(featureLpq);
    }

    private static void markFeatureInitialized(@NotNull Project project, @NotNull String featureLpq) {
        INITIALIZED_FEATURES.computeIfAbsent(project,
                p -> ConcurrentHashMap.newKeySet()).add(featureLpq);
    }

    private static boolean isFullyInitialized(@NotNull Project project) {
        return Boolean.TRUE.equals(FULLY_INITIALIZED.get(project));
    }

    private static void setFullyInitialized(@NotNull Project project, boolean value) {
        FULLY_INITIALIZED.put(project, value);
    }

    /**
     * Internal data structure for batching reference data.
     * Using a record for immutability and performance (Java 16+).
     */
    private record ReferenceData(
            @NotNull PsiElement element,
            @NotNull PsiFile containingFile,
            @NotNull String originatingFilePath
    ) {
    }
}
