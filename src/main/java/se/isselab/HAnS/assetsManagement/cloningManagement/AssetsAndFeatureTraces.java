package se.isselab.HAnS.assetsManagement.cloningManagement;

import com.intellij.psi.*;

import java.util.ArrayList;

public class AssetsAndFeatureTraces {
    public static PsiMethod clonedMethod;
    public static PsiClass clonedClass;
    public static String sourceProjectName;

    public static String sourcePath;
    public static ArrayList<String> subFeatureTrace;
    public static ArrayList<PsiElement> featuresAnnotations;

    public static void resetAssetClones() {
        clonedClass = null;
        clonedMethod = null;
        sourceProjectName = null;
        sourcePath = null;
    }
    public static void resetTraces() {
        subFeatureTrace = null;
        featuresAnnotations = null;
        FeaturesCodeAnnotations.getInstance().clearFeatures();
    }
}
