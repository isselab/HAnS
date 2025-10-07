/*
Copyright 2021 Herman Jansson & Johan Martinson

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
package se.isselab.HAnS.featureModel;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Consumer;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.intellij.psi.search.FilenameIndex.getAllFilesByExt;
import static com.intellij.psi.search.FilenameIndex.getVirtualFilesByName;
import static com.intellij.psi.search.GlobalSearchScope.projectScope;

public final class FeatureModelUtil {

    private FeatureModelUtil() {
    }

    /**
     * Searches for {@link FeatureModelFeature} instances in the given {@link Project} whose LPQ (Least Partially Qualified)
     * exactly matches the specified {@code lpq} string.
     * <p>
     * This method scans all files of type {@code FeatureModelFileType} within the project's global scope and collects
     * all {@code FeatureModelFeature} elements. It then compares each feature's LPQ text with the provided {@code lpq}
     * and returns those that match exactly.
     * </p>
     *
     * @param project the IntelliJ {@link Project} context used to search for feature model files.
     * @param lpq the Least Partially Qualified string to match against each feature's LPQ text.
     * @return a list of {@link FeatureModelFeature} instances whose LPQ text matches the given {@code lpq}.
     */
    public static List<FeatureModelFeature> findLPQ(Project project, String lpq) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = getFeatureModelFiles(project);
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (featureModelFile == null) continue;

            Collection<FeatureModelFeature> features = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class);
            features.stream()
                    .filter(feature -> lpq.equals(feature.getLPQText()))
                    .forEach(result::add);
        }
        return result;
    }

    /**
     * Searches for {@link FeatureModelFeature} instances in the given {@link Project} that match the specified LPQ (Least Partially Qualified).
     * <p>
     * This method scans all files of type {@code FeatureModelFileType} within the project scope and attempts to find features
     * whose names or LPQ text match the provided {@code lpq} string. It handles both exact and hierarchical matches.
     * </p>
     *
     * <p>
     * Matching logic:
     * <ul>
     *   <li>If exactly one feature matches the end of the LPQ string, it checks if the full LPQ ends with "::lpq" or equals lpq.</li>
     *   <li>If multiple features match, it checks if the LPQ starts with the parent feature name and ends with the full LPQ.</li>
     * </ul>
     * </p>
     *
     * @param project the IntelliJ {@link Project} context used to search for feature model files.
     * @param lpq the Least Partially Qualified string to match against feature names and LPQ text.
     * @return a list of {@link FeatureModelFeature} instances that match the given LPQ.
     */
    public static List<FeatureModelFeature> findFullLPQ(Project project, String lpq) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = getFeatureModelFiles(project);
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);

            if(featureModelFile == null) continue;

            var selectedFeatures = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class).stream()
                    .filter(Objects::nonNull)
                    .filter(featureModelFeature -> lpq.endsWith(featureModelFeature.getFeatureName()))
                    .toList();

            result.addAll(processSelectedFeatures(lpq, selectedFeatures));
        }
        return result;
    }

    private static Collection<FeatureModelFeature> processSelectedFeatures(String lpq, List<FeatureModelFeature> selectedFeatures) {
        var result = new ArrayList<FeatureModelFeature>();
        if (selectedFeatures.size() == 1) {
            var feature = selectedFeatures.getFirst();
            var fullLPQ = feature.getFullLPQText();
            if (fullLPQ.endsWith("::" + lpq) || fullLPQ.equals(lpq)) {
                result.add(feature);
            }
        } else if (selectedFeatures.size() > 1) {
            selectedFeatures.forEach(feature -> {
                var fullLPQ = feature.getFullLPQText();
                if (feature.getParent() instanceof FeatureModelFeature parent) {
                    var parentFeatureName = parent.getFeatureName();
                    if (lpq.startsWith(parentFeatureName) && fullLPQ.endsWith(lpq)) {
                        result.add(feature);
                    }
                }
            });
        }
        return result;
    }


    /**
     * Retrieves all {@link FeatureModelFeature} instances defined in the current {@link Project}.
     * <p>
     * This method scans all files of type {@code FeatureModelFileType} within the project's global scope
     * and collects every {@code FeatureModelFeature} found in those files.
     * </p>
     *
     * @param project the IntelliJ {@link Project} context used to search for feature model files.
     * @return a list of all {@link FeatureModelFeature} instances found in the project.
     */
    public static List<FeatureModelFeature> findFeatures(Project project) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles = getFeatureModelFiles(project);
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (featureModelFile != null) {
                Collection<FeatureModelFeature> features = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class);
                result.addAll(features);
            }
        }
        return result;
    }

    private static Collection<VirtualFile> getFeatureModelFiles(Project project) {
        return FileTypeIndex.getFiles(FeatureModelFileType.INSTANCE, GlobalSearchScope.allScope(project));
    }


    public static void findFeatureModelAsync(@NotNull Project project, @NotNull Consumer<PsiFile> callback) {
        ReadAction.nonBlocking(() -> findFeatureModel(project))
                .inSmartMode(project)
                .finishOnUiThread(ModalityState.defaultModalityState(), callback)
                .submit(AppExecutorUtil.getAppExecutorService());
    }

    public static PsiFile findFeatureModel(@NotNull Project project) {
        var allFilenames = getVirtualFilesByName(".feature-model", projectScope(project));
        if (!allFilenames.isEmpty()) {
            return PsiManager.getInstance(project).findFile(allFilenames.iterator().next());
        }
        Collection<VirtualFile> virtualFileCollection = getAllFilesByExt(project, "feature-model");
        if (!virtualFileCollection.isEmpty()) {
            return PsiManager.getInstance(project).findFile(virtualFileCollection.iterator().next());
        }
        Collection<VirtualFile> virtualFiles = getFeatureModelFiles(project);
        if (!virtualFiles.isEmpty()) {
            return PsiManager.getInstance(project).findFile(virtualFiles.iterator().next());
        }
        return null;
    }
}