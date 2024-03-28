/*
Copyright 2021 Herman Jansson & Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.referencing;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class FeatureReferenceUtil {

    private static String lpq = null;
    private static PsiElement origin = null;
    private static boolean addingOrDeleting = false;

    private static Map<FeatureModelFeature, List<PsiReference>> mapToRename = new HashMap<>();
    private static Map<FeatureModelFeature, List<PsiReference>> mapToRemove = new HashMap<>();

    private static Map<String, List<PsiReference>> mapToRenameWhenAdding = new HashMap<>();


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

    public static boolean getAddingOrDeleting() {
        return addingOrDeleting;
    }

    public static void reset() {
        lpq = null;
        origin = null;
        addingOrDeleting = false;
    }

    public static Map<FeatureModelFeature, List<PsiReference>> getElementsToRename() {
        return mapToRename;
    }

    public static Map<FeatureModelFeature, List<PsiReference>> getElementsToRemove() {
        return mapToRemove;
    }

    public static Map<String, List<PsiReference>> getMapToRenameWhenAdding() {
        return mapToRenameWhenAdding;
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
            se.isselab.HAnS.featureModel.psi.FeatureModelFeature tmpFeature = FeatureModelElementFactory.createFeature(e[0].getProject(), newName);
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

    public static void setElementsToRenameWhenRenaming(FeatureModelFeature element, String newName) {
        Map<FeatureModelFeature, List<PsiReference>> toRename = new HashMap<>();

        System.out.println("Will rename");

        List<FeatureModelFeature> elementsToRename = getElementsToRenameWhenRenaming(element, newName);

        for (FeatureModelFeature e : elementsToRename) {
            System.out.println(e.getLPQText());
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
                    else if (((FeatureModelFeature) e).getLPQText().contains(Objects.requireNonNull(element.getNode().findChildByType(FeatureModelTypes.FEATURENAME)).getText())) {
                        elementsToRename.add((FeatureModelFeature) e);
                    }
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

    public static void setElementsToRenameWhenAdding(FeatureModelFeature element, String newName) {
        Map<FeatureModelFeature, List<PsiReference>> toRename = new HashMap<>();

        List<FeatureModelFeature> elementsToRename = getElementsToRenameWhenAdding(element, newName);

        for (FeatureModelFeature e : elementsToRename) {
            List<PsiReference> referencedElements = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(e)) {
                referencedElements.add(reference);
            }
            toRename.put(e, referencedElements);
        }

        addingOrDeleting = true;

        mapToRename = toRename;
    }

    private static List<FeatureModelFeature> getElementsToRenameWhenAdding(FeatureModelFeature element, String newName) {
        List<FeatureModelFeature> elementsToRename= new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature && ((FeatureModelFeature) e).getLPQText().contains(newName)){
                    elementsToRename.add((FeatureModelFeature) e);
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

    public static void delete() {
        Map<FeatureModelFeature, List<PsiReference>> toDelete = getElementsToRemove();

        for (Map.Entry<FeatureModelFeature, List<PsiReference>> entry : toDelete.entrySet()) {

            for (PsiReference reference:entry.getValue()) {
                InjectedLanguageManager injManager = InjectedLanguageManager.getInstance(entry.getKey().getProject());

                PsiLanguageInjectionHost host = injManager.getInjectionHost(reference.getElement());
                if (host != null) { // if comment annotation
                    host.delete();
                }
                else { // if feature to folder/file annotation
                    if (reference.getElement().getNextSibling() != null && reference.getElement().getNextSibling().getText().equals(" ")) {
                        reference.getElement().getNextSibling().delete(); // remove space after element
                    }
                    reference.getElement().delete();
                }
            }
        }
    }



    public static void setElementsToDelete(FeatureModelFeature element) {
        Map<FeatureModelFeature, List<PsiReference>> toDelete = new HashMap<>();

        List<PsiElement> elementsToDelete= getElementsToDelete(element)[0];
        GlobalSearchScope scope = GlobalSearchScope.allScope(element.getProject());

        for (PsiElement e : elementsToDelete) {
            List<PsiReference> referencedElements = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(e, scope)) {
                referencedElements.add(reference);
            }
            toDelete.put(((FeatureModelFeature) e), referencedElements);
        }

        addingOrDeleting = true;
        mapToRemove = toDelete;
    }

    public static void setElementsToRenameWhenDeleting(FeatureModelFeature element) {
        Map<FeatureModelFeature, List<PsiReference>> toRename = new HashMap<>();

        List[] result = getElementsToDelete(element);
        List<PsiElement> toRemove = result[0];
        List<String> lpqToRemove = result[1];

        List<FeatureModelFeature> elementsToRename = new ArrayList<>();
        for (PsiElement feature : toRemove) {
            elementsToRename.addAll(getElementsToRenameWhenDeleting(((FeatureModelFeature) feature), lpqToRemove));
        }

        for (FeatureModelFeature e : elementsToRename) {
            List<PsiReference> referencedElements = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(e)) {
                referencedElements.add(reference);
            }
            toRename.put(e, referencedElements);
        }

        mapToRename = toRename;
    }

    private static List<FeatureModelFeature> getElementsToRenameWhenDeleting(FeatureModelFeature element, List<String> lpqToRemove) {
        List<FeatureModelFeature> elementsToRename= new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature){
                    if (((FeatureModelFeature) e).getLPQText().contains(element.getFeatureName()) &&
                        !(lpqToRemove.contains(((FeatureModelFeature) e).getLPQText()))) {
                        elementsToRename.add((FeatureModelFeature) e);
                    }
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }


    private static List[] getElementsToDelete(FeatureModelFeature element) {
        List<PsiElement> elementsToDelete= new ArrayList<>();
        List<String> lpqToDelete= new ArrayList<>();
        traverseFeatureWithChildren(element, elementsToDelete, lpqToDelete);

        List[] result = new List[2];
        result[0] = elementsToDelete;
        result[1] = lpqToDelete;
        return result;
    }

    private static void traverseFeatureWithChildren(FeatureModelFeature parentFeature, List<PsiElement> featureList, List<String> lpqList) {
        featureList.add(parentFeature);
        lpqList.add(parentFeature.getLPQText());
        PsiElement[] children = parentFeature.getChildren();
        for (PsiElement child : children) {
            traverseFeatureWithChildren(((FeatureModelFeature) child), featureList, lpqList);
        }
    }

    public static void updateChildAfterAdded(List<String> oldlpqList, List<String> newlpqList) {
        addingOrDeleting = true;
        Map<String, List<PsiReference>> toUpdate = getMapToRenameWhenAdding();

        for (int i = 0; i < oldlpqList.size(); i++) {

            for (Map.Entry<String, List<PsiReference>> entry : toUpdate.entrySet()) {
                if (entry.getKey().equals(oldlpqList.get(i))) {

                    for (PsiReference reference:entry.getValue()) {
                        reference.handleElementRename(newlpqList.get(i));
                    }

                    toUpdate.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    public static void setElementsToRenameAfterAddingWithChildren(FeatureModelFeature element) {
        Map<String, List<PsiReference>> toUpdate = new HashMap<>();

        List<PsiElement> elementsToUpdate= getElementsToRenameAfterAddingWithChildren(element);
        for (PsiElement e : elementsToUpdate) {
            List<PsiReference> referencedElements = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(e)) {
                referencedElements.add(reference);
            }
            toUpdate.put(((FeatureModelFeature) e).getLPQText(), referencedElements);
        }
        mapToRenameWhenAdding = toUpdate;
    }

    private static List<PsiElement> getElementsToRenameAfterAddingWithChildren(FeatureModelFeature element) {
        List<PsiElement> elementsToUpdate= new ArrayList<>();
        List<String> lpqToDelete= new ArrayList<>();

        traverseFeatureWithChildren(element, elementsToUpdate, lpqToDelete);
        return elementsToUpdate;
    }

}
