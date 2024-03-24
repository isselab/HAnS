package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class CloneManager {
    public static void CloneFileAssets(Project project, PsiFile psiFile,String sourceProjectName, String sourcePath, String targetPath){
        TracingHandler tracingHandler = new TracingHandler();
        FeatureModelHandler featureModelHandler = new FeatureModelHandler(project);
        FeaturesAnnotationsExtractor.saveExtractedFeatureAnnotations(psiFile);
        AssetsAndFeatureTraces.featuresAnnotations = featureModelHandler.findFeatureToFileMappings(psiFile);
        if(AssetsAndFeatureTraces.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsAndFeatureTraces.featuresAnnotations);
        }
        featureModelHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(sourceProjectName, project.getName());
        tracingHandler.storeCloneTrace(project, sourceProjectName, sourcePath, targetPath);
        AssetsAndFeatureTraces.resetTraces();
    }
    public static void CloneFolderAssets(Project project, PsiElement dir, String sourceProjectName, String sourcePath, String targetPath){
        TracingHandler tracingHandler = new TracingHandler();
        FeatureModelHandler featureModelHandler = new FeatureModelHandler(project);
        FeaturesAnnotationsExtractor.saveExtractedFeatureAnnotations(dir);
        AssetsAndFeatureTraces.featuresAnnotations = featureModelHandler.findFeatureToFolderMappings((PsiDirectory) dir);
        if(AssetsAndFeatureTraces.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsAndFeatureTraces.featuresAnnotations);
        }
        featureModelHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(sourceProjectName, project.getName());
        tracingHandler.storeCloneTrace(project, sourceProjectName, sourcePath, targetPath);
        AssetsAndFeatureTraces.resetTraces();
    }
    public static void CloneClassAssets(Project project, String sourceProjectName, String sourcePath, String targetPath, PsiClass copiedClass, String currentClassName){
        TracingHandler tracingHandler = new TracingHandler();
        FeatureModelHandler featureModelHandler = new FeatureModelHandler(project);
        featureModelHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(sourceProjectName, project.getName());
        String className = (!currentClassName.isEmpty()) ? currentClassName : copiedClass.getName();
        sourcePath = sourcePath + "/" + copiedClass.getName();
        targetPath = targetPath + "/" + className;
        tracingHandler.storeCloneTrace(project, sourceProjectName, sourcePath, targetPath);
        AssetsAndFeatureTraces.resetTraces();
    }
    public static void CloneMethodAssets(Project project, String sourceProjectName, String sourcePath, String targetPath, String className, PsiMethod copiedMethod, String currentClassName, String currentMethodName){
        TracingHandler tracingHandler = new TracingHandler();
        FeatureModelHandler featureModelHandler = new FeatureModelHandler(project);
        featureModelHandler.addFeaturesToFeatureModel();
        tracingHandler.createCopyFeatureTrace(sourceProjectName, project.getName());
        String methodName = (!currentMethodName.isEmpty()) ? currentMethodName : copiedMethod.getName();
        sourcePath = sourcePath + "/" + className + "/" + copiedMethod.getName();
        targetPath = targetPath + "/" + currentClassName + "/" + methodName;
        tracingHandler.storeCloneTrace(project, sourceProjectName, sourcePath, targetPath);
        AssetsAndFeatureTraces.resetTraces();
    }
}
