package se.ch.HAnS.referencing;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureReferenceUtil {

    private static String lpq = null;
    private static PsiElement origin = null;

    private static Map<FeatureModelFeature, List<PsiReference>> mapToRename = new HashMap<>();

    public static String getLPQ(FeatureModelFeature feature, String newName) {
        if (lpq == null) {
            return setLPQ(feature, newName);
        }
        else {
            return lpq;
        }
    }

    public static PsiElement getOrigin() {
        return origin;
    }

    public static void reset() {
        lpq = null;
        origin = null;
    }

    public static Map<FeatureModelFeature, List<PsiReference>> getElementsToRename() {
        return mapToRename;
    }

    private static String setLPQ(FeatureModelFeature feature, String newName) {
        PsiElement fileCopy = feature.getContainingFile().copy();
        final FeatureModelFeature[] e = new FeatureModelFeature[1];
        fileCopy.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeature){
                    if (((FeatureModelFeature) element).getLPQText().equals(feature.getLPQText())) {
                        e[0] = (FeatureModelFeature) element;
                    }
                }
                super.visitElement(element);
            }
        });
        ASTNode featureNode = e[0].getNode().findChildByType(FeatureModelTypes.FEATURENAME);
        if (featureNode != null) {
            se.ch.HAnS.featureModel.psi.FeatureModelFeature tmpFeature = FeatureModelElementFactory.createFeature(e[0].getProject(), newName);
            ASTNode newKeyNode = tmpFeature.getFirstChild().getNode();
            e[0].getNode().replaceChild(featureNode, newKeyNode);
        }
        origin = feature;
        return lpq = e[0].getLPQText();
    }

    public static void rename() {
        Map<FeatureModelFeature, List<PsiReference>> toRename = getElementsToRename();

        for (Map.Entry<FeatureModelFeature, List<PsiReference>> entry : toRename.entrySet()) {
            String newLPQ = entry.getKey().getLPQText();
            for (PsiReference reference:entry.getValue()) {
                reference.handleElementRename(newLPQ);
            }
        }
    }

    public static void setElementsToRename(FeatureModelFeature element, String newName) {
        Map<FeatureModelFeature, List<PsiReference>> toRename = new HashMap<>();

        List<FeatureModelFeature> elementsToRename = getElementsToRenameWhenRenaming(element, newName);

        for (FeatureModelFeature e : elementsToRename) {
            List<PsiReference> referencedElements = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(e)) {
                referencedElements.add(reference);
            }
            toRename.put(e, referencedElements);
        }

        mapToRename = toRename;
    }

    private static List<FeatureModelFeature> getElementsToRenameWhenRenaming(FeatureModelFeature element, String newName) {
        List<FeatureModelFeature> elementsToRename= new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature){
                    if (((FeatureModelFeature) e).getLPQText().contains(newName)) {
                        elementsToRename.add((FeatureModelFeature) e);
                    }
                    else if (((FeatureModelFeature) e).getLPQText().contains(element.getNode().getText())) {
                        elementsToRename.add((FeatureModelFeature) e);
                    }
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

    private static List<FeatureModelFeature> getElementsToRenameWhenAdding(FeatureModelFeature element, String newName) {
        List<FeatureModelFeature> elementsToRename= new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature){
                    elementsToRename.add((FeatureModelFeature) e);
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

    private static List<PsiElement> getElementsToRenameWhenDeleting(FeatureModelFeature element, String newName) {
        List<PsiElement> elementsToRename= new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature){
                    if (((FeatureModelFeature) e).getLPQText().contains(newName)) {
                        elementsToRename.add(e);
                    }
                    else if (((FeatureModelFeature) e).getLPQText().contains(element.getNode().getText())) {
                        elementsToRename.add(e);
                    }
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

}
