package se.isselab.HAnS.vpIntegration;

import com.intellij.psi.*;

import java.util.ArrayList;
import java.util.List;

public class AssetsToClone {
    public static PsiFile clonedFile;
    public static PsiDirectory clonedDirectory;
    public static PsiMethod clonedMethod;
    public static PsiClass clonedClass;
    public static List<PsiElement> elementsInRange;
    public static String subAssetTrace;
    public static ArrayList<String> subFeatureTrace;
    public static ArrayList<PsiElement> featuresAnnotations;

    public static void resetClones() {
        clonedFile = null;
        clonedDirectory = null;
        clonedClass = null;
        clonedMethod = null;
        elementsInRange = null;
        subAssetTrace = null;
        subFeatureTrace = null;
        featuresAnnotations = null;
        FeaturesCodeAnnotations.getInstance().clearFeatures();
    }
}
