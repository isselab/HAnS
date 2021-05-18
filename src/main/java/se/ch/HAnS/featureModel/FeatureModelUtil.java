package se.ch.HAnS.featureModel;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelFile;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

import java.util.*;

public class FeatureModelUtil {

    private static String lpq = null;
    private static PsiElement origin = null;
    private static PsiElement psiTreeCopy = null;

    public static List<FeatureModelFeature> findLPQ(Project project, String lpq) {
        List<FeatureModelFeature> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(FeatureModelFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            FeatureModelFile featureModelFile = (FeatureModelFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (featureModelFile != null) {
                Collection<FeatureModelFeature> features = PsiTreeUtil.collectElementsOfType(featureModelFile, FeatureModelFeature.class);
                for (FeatureModelFeature feature : features) {
                    if (lpq.equals(feature.getLPQText())) {
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

    public static String getLPQ(FeatureModelFeature feature, String newName) {
        if (origin == null) {
            return setData(feature, newName);
        }
        else if (origin == feature) {
            return lpq;
        }
        else {
            return getUpdatedLPQ(feature);
        }
    }

    public static PsiElement getOrigin() {
        return origin;
    }

    public static PsiElement getPsiTreeCopy() {
        return psiTreeCopy;
    }

    private static String setData(FeatureModelFeature element, String newName) {
        origin = element;
        psiTreeCopy = setPsiTreeCopy(element, newName);
        return lpq;
    }

    private static PsiElement setPsiTreeCopy(FeatureModelFeature feature, String newName) {
        PsiElement fileCopy = feature.getContainingFile().copy();
        final FeatureModelFeatureImpl[] e = new FeatureModelFeatureImpl[1];
        fileCopy.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeatureImpl){
                    if (((FeatureModelFeatureImpl) element).getLPQText().equals(feature.getLPQText())) {
                        e[0] = (FeatureModelFeatureImpl) element;
                    }
                }
                super.visitElement(element);
            }
        });
        ASTNode featureNode = e[0].getNode().findChildByType(FeatureModelTypes.FEATURENAME);
        if (featureNode != null) {
            FeatureModelFeature tmpFeature = FeatureModelElementFactory.createFeature(e[0].getProject(), newName);
            ASTNode newKeyNode = tmpFeature.getFirstChild().getNode();
            e[0].getNode().replaceChild(featureNode, newKeyNode);
        }
        lpq = e[0].getLPQText();
        return fileCopy;
    }

    private static String getUpdatedLPQ(PsiElement feature) {
        String[] e = new String[1];
        psiTreeCopy.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeatureImpl){
                    if (comparePaths(feature, element)) {
                        e[0] = ((FeatureModelFeatureImpl) element).getLPQText();
                    }
                }
                super.visitElement(element);
            }
        });
        return e[0];
    }

    private static boolean comparePaths(PsiElement realElement, PsiElement copyElement) {
        return getPath(realElement).equals(getPath(copyElement));
    }

    private static List<String> getPath(PsiElement feature) {
        List<String> list = new ArrayList<>();
        if (feature instanceof PsiFile) {
            return list;
        }
        list.add(Objects.requireNonNull(feature.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText());
        list.addAll(getPath(feature.getParent()));
        return list;
    }

    public static void reset() {
        lpq = null;
        psiTreeCopy = null;
        origin = null;
    }

}