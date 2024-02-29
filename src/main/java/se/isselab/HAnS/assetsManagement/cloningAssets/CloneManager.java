package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;

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
        AssetsToClone.resetClones();
    }
    public static void CloneFolderAssets(){

    }
    public static void CloneClassAssets(Project project, PsiFile file, PsiClass copiedClass){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        CloningEditorMenuHandler.saveExtractedFeatureAnnotations(copiedClass);
        featuresHandler.addFeaturesToFeatureModel();
    }
    public static void CloneMethodAssets(Project project, PsiFile file, PsiMethod copiedMethod){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        CloningEditorMenuHandler.saveExtractedFeatureAnnotations(copiedMethod);
        featuresHandler.addFeaturesToFeatureModel();
    }
}
