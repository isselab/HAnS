package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.psi.*;
import se.isselab.HAnS.assetsManagement.AssetsManagementPreferences;

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
    public static boolean isAllPreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("All");
    }
    public static boolean isClonePreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("clone");
    }
    public static boolean isPropagatePreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("propagate");
    }
    public static boolean isShowClonePreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("showClone");
    }
    public static boolean isCloneAndPropagatePreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("cloneAndPropagate");
    }
    public static boolean isCloneAndShowClonePreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("cloneAndShowClone");
    }
    public static boolean isShowCloneAndPropagatePreference(){
        return AssetsManagementPreferences.properties.getValue(AssetsManagementPreferences.ASSETS_MANAGEMENT_PREF_KEY, "none").equals("showCloneAndPropagate");
    }
}
