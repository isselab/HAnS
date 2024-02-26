package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;

public class CloneManager {
    public static void CloneFileAssets(Project project, PsiFile psiFile,String sourceProjectName, String sourcePath, String targetPath){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        CloningProjectMenuHandler.saveExtractedFeatureAnnotations(psiFile);
        AssetsToClone.featuresAnnotations = featuresHandler.findFeatureToFileMappings(psiFile);
        if(AssetsToClone.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsToClone.featuresAnnotations);
        }
        featuresHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(project, sourceProjectName);
        tracingHandler.storeCopyPasteFileTrace(project, sourcePath, targetPath);
    }
}
