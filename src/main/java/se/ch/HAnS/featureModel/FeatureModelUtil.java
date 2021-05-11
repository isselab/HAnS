package se.ch.HAnS.featureModel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FeatureModelUtil {

    public static List<FeatureModelFeature> findLPQs(Project project, String lpq) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(FeatureModelFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (featureModelFile != null) {
                Collection<FeatureModelFeature> features = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class);
                for (FeatureModelFeature feature : features) {
                    if (lpq.equals(feature.getLPQ())) {
                        result.add(feature);
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
                Collection<FeatureModelFeature> features = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class);
                result.addAll(features);
            }
        }
        return result;
    }

}