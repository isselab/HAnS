package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class CloneManager {
    public static void CloneFileAssets(Project project, PsiFile psiFile,String sourceProjectName, String sourcePath, String targetPath){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        FeaturesAnnotationsExtractor.saveExtractedFeatureAnnotations(psiFile);
        AssetsToClone.featuresAnnotations = featuresHandler.findFeatureToFileMappings(psiFile);
        if(AssetsToClone.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsToClone.featuresAnnotations);
        }
        featuresHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(project, sourceProjectName, project.getName());
        tracingHandler.storeCopyPasteFileTrace(project, sourcePath, targetPath);
        AssetsToClone.resetClones();
    }
    public static void CloneFolderAssets(Project project, PsiElement dir, String sourceProjectName, String sourcePath, String targetPath){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        FeaturesAnnotationsExtractor.saveExtractedFeatureAnnotations(dir);
        AssetsToClone.featuresAnnotations = featuresHandler.findFeatureToFolderMappings((PsiDirectory) dir);
        if(AssetsToClone.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsToClone.featuresAnnotations);
        }
        featuresHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(project, sourceProjectName, project.getName());
        tracingHandler.storeCopyPasteFileTrace(project, sourcePath, targetPath);
        AssetsToClone.resetClones();
    }
    public static void CloneClassAssets(Project project, String sourceProjectName, String sourcePath, String targetPath, PsiClass copiedClass, String currentClassName){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        featuresHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(project, sourceProjectName, project.getName());
        String className = (currentClassName.length() > 0) ? currentClassName : copiedClass.getName();
        sourcePath = sourcePath + "/" + copiedClass.getName();
        targetPath = targetPath + "/" + className;
        tracingHandler.storeCopyPasteFileTrace(project, sourcePath, targetPath);
        AssetsToClone.resetClones();
    }
    public static void CloneMethodAssets(Project project, String sourceProjectName, String sourcePath, String targetPath, String className, PsiMethod copiedMethod, String currentClassName, String currentMethodName){
        TracingHandler tracingHandler = new TracingHandler();
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        featuresHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(project, sourceProjectName, project.getName());
        String methodName = (currentMethodName.length() > 0) ? currentMethodName : copiedMethod.getName();
        sourcePath = sourcePath + "/" + className + "/" + copiedMethod.getName();
        targetPath = targetPath + "/" + currentClassName + "/" + methodName;
        tracingHandler.storeCopyPasteFileTrace(project, sourcePath, targetPath);
        AssetsToClone.resetClones();
    }
}
