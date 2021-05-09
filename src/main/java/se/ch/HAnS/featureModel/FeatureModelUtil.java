package se.ch.HAnS.featureModel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelFile;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

import java.util.*;

public class FeatureModelUtil {
    /**
     * Searches the entire project for FeatureModel language files with instances of the Simple property with the given key.
     * TODO: Adjust for LPQ so that annotators can use the LPQs and not featurenames
     * @param project current project
     * @param featurename     to check
     * @return matching properties
     */
    public static List<FeatureModelFeature> findFeatures(Project project, String featurename) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(FeatureModelFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (featureModelFile != null) {
                @NotNull Collection<FeatureModelFeature> properties = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class);
                for (FeatureModelFeature property : properties) {
                    if (featurename.equals(property.getFeatureName())) {
                        result.add(property);
                    }
                }
            }
        }
        return result;
    }

    public static List<FeatureModelFeature> findFeatures(Project project) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(FeatureModelFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (featureModelFile != null) {
                FeatureModelFeature[] properties = PsiTreeUtil.getChildrenOfType(featureModelFile, FeatureModelFeature.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}
