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
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.FeatureAnnotationToDelete;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;
import se.isselab.HAnS.metrics.FeatureTangling;

import javax.print.Doc;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class FeatureReferenceUtil {

    private static String lpq = null;
    private static PsiElement origin = null;
    private static boolean addingOrDeleting = false;

    private static Map<FeatureModelFeature, List<PsiReference>> mapToRename = new HashMap<>();
    private static Map<FeatureModelFeature, List<PsiReference>> mapToRemove = new HashMap<>();
    private static Map<FeatureModelFeature, ArrayList<Object>> mapToDeleteWithCode = new HashMap<>();

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

    public static Map<FeatureModelFeature, ArrayList<Object>> getMapToDeleteWithCode() { return mapToDeleteWithCode; }

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

    public static void deleteWithCode() {
        System.out.println("DELETE WITH CODE --------------");
        Map<FeatureModelFeature, ArrayList<Object>> featureToAnnotations = getMapToDeleteWithCode();
        // feature -> list of either PsiReference or FeatureAnnotationToDelete
        for (Map.Entry<FeatureModelFeature, ArrayList<Object>> entry : featureToAnnotations.entrySet()) {
            FeatureModelFeature feature = entry.getKey();
            ArrayList<Object> annotations = entry.getValue();
            Map<Document, ArrayList<Range>> codeAnnotations = (Map<Document, ArrayList<Range>>) annotations.get(0);
            List<PsiReference> fileFolderAnnotation = (List<PsiReference>) annotations.get(1);
            codeAnnotations.entrySet().forEach(codeAnnotation -> {

//                FeatureAnnotationToDelete annotationElement = (FeatureAnnotationToDelete) annotation;
                Document document = codeAnnotation.getKey();
                System.out.println(document);
//                System.out.println(annotationElement.getStartLine());
//                System.out.println(annotationElement.getEndLine());
                ArrayList<Range> rangesOfAnnotations = codeAnnotation.getValue();
                String newDocumentText = "";
                String documentText = document.getText();
                int start = 0;
                int end = document.getLineEndOffset(document.getLineCount()-1);
                for (Range range : rangesOfAnnotations) {
                    System.out.println(start);
                    System.out.println(end);
                    end = document.getLineStartOffset((int) range.getMinimum());
                    newDocumentText.concat(documentText.substring(start, end));
                    start = document.getLineStartOffset((int) range.getMaximum()+1);
                }
                end = document.getLineEndOffset(document.getLineCount()-1);
                newDocumentText.concat(documentText.substring(start, end));

//                int start = document.getLineStartOffset(annotationElement.getStartLine());
//                int end = document.getLineEndOffset(annotationElement.getEndLine());
//                System.out.println(start);
//                System.out.println(end);
//                String documentText = document.getText();
//                String sub = documentText.substring(0, start);
//                String remainder = documentText.substring(end);
//                System.out.println(sub);
//                System.out.println(remainder);
//                String newContent = sub.concat(remainder);
//                System.out.println(newContent);
                System.out.println(newDocumentText);
                Runnable r = () -> {
                    document.setReadOnly(false);
                    document.setText(newDocumentText);
                };
//                    WriteCommandAction.runWriteCommandAction(feature.getProject(), r);
            });


            fileFolderAnnotation.forEach(annotation -> {
                PsiReference reference = annotation;
                if (reference.getElement().getNextSibling() != null &&
                    reference.getElement().getNextSibling().getText().equals(" ")) {
                    reference.getElement().getNextSibling().delete(); // remove space after element
                }
                reference.getElement().delete(); // remove element
            });
        }
    }

    public static void setMapToDeleteWithCode(FeatureModelFeature feature) {
        List<PsiElement> elementsToDelete = getElementsToDelete(feature)[0];
        Project projectInstance = feature.getProject();
        elementsToDelete.stream().forEach(child -> {
            FeatureModelFeature childFeature = (FeatureModelFeature) child;

            FeatureFileMapping fileToAnnotation = FeatureLocationManager.getFeatureFileMapping(projectInstance, childFeature);
            Map<Document, ArrayList<Range>> codeAnnotations = getCodeAnnotations(fileToAnnotation, childFeature); // code Annotations

            System.out.println("codeAnnotations----]]]]]]]]]]]]]");
            codeAnnotations.entrySet().forEach(codeAnnotation -> {
                System.out.println(codeAnnotation.getKey());
                System.out.println(Arrays.toString(codeAnnotation.getValue().toArray()));
            });

            List<PsiReference> fileAndFolderAnnotations = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(child)) {
                InjectedLanguageManager injManager = InjectedLanguageManager.getInstance(projectInstance);

                PsiLanguageInjectionHost host = injManager.getInjectionHost(reference.getElement());
                if (host == null) { // if it's file or folder annotation, so not an injection
                    fileAndFolderAnnotations.add(reference);
                }
            }
            System.out.println(childFeature.getLPQText());

            System.out.println(Arrays.toString(fileAndFolderAnnotations.toArray()));
            mapToDeleteWithCode.put(childFeature, new ArrayList<>());
            mapToDeleteWithCode.get(childFeature).add(codeAnnotations);
            mapToDeleteWithCode.get(childFeature).add(fileAndFolderAnnotations);
        });
    }

    private static Map<Document, ArrayList<Range>> getCodeAnnotations(FeatureFileMapping fileToAnnotation, FeatureModelFeature feature) {
        Map<String, ArrayList<Range>> mapToDrop = new HashMap<>(); // documentName -> ranges of annotation blocks

        for (FeatureLocation fm : fileToAnnotation.getFeatureLocations()) {
            fm.getFeatureLocations().stream().forEach(block -> {
                if (fm.getAnnotationType().equals(FeatureFileMapping.AnnotationType.code)) {

                    ArrayList<Range> entry = mapToDrop.get(fm.getMappedPath());

                    if (entry == null) {
                        entry = new ArrayList<Range>();
                        entry.add(Range.between(block.getStartLine(), block.getEndLine()));
                        mapToDrop.put(fm.getMappedPath(), entry);
                    } else {
                        AtomicInteger start = new AtomicInteger();
                        AtomicInteger end = new AtomicInteger();
                        entry.forEach(entr -> {
                            ArrayList<Integer> result = getRange(block.getStartLine(), block.getEndLine(), (int) entr.getMinimum(), (int) entr.getMaximum());
                            if (result == null) {
                                start.set(block.getStartLine());
                                end.set(block.getEndLine());
                            } else {
                                start.set(result.get(0));
                                end.set(result.get(1));
                            }
                            System.out.println((int) entr.getMinimum() + " "+ (int) entr.getMaximum());
                            System.out.println(start + " " + end);
                        });
                        entry.add(Range.between(start.get(), end.get()));
                    }
//                    FeatureAnnotationToDelete elementToDrop = new FeatureAnnotationToDelete();

//                    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(fm.getMappedPath());
//                    elementToDrop.setDocument(FileDocumentManager.getInstance().getDocument(file));

//                    elementToDrop.setStartLine(block.getStartLine());
//                    elementToDrop.setEndLine(block.getEndLine());
//                    mapToDrop.add(elementToDrop);
                }
            });
        }
        mapToDrop.values().stream().forEach(value -> {
            value.sort((c1, c2) -> {
                if((int) c1.getMinimum() < (int) c2.getMinimum()) { return -1; }
                else if((int) c1.getMinimum() == (int) c2.getMinimum()) { return 0; }
                return 1;
            });
        });
        // substitute string to document
        Map<Document, ArrayList<Range>> result = mapToDrop.entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(entry.getKey());
                    return FileDocumentManager.getInstance().getDocument(file);
                }, entry -> entry.getValue()));
        return result;
    }

    public static Set<FeatureAnnotationToDelete> setElementsToDropWhenDeletingMain(FeatureModelFeature parentFeature) {
        List<PsiElement> featureWithKids = new ArrayList<>();
        List<String> featureWithKidsLPQs = new ArrayList<>();
        traverseFeatureWithChildren(parentFeature, featureWithKids, featureWithKidsLPQs);
        Set<FeatureAnnotationToDelete> result = new HashSet<>();
        for (PsiElement feature : featureWithKids) {
            Set<FeatureAnnotationToDelete> places = setElementsToDropWhenDeleting((FeatureModelFeature) feature, featureWithKidsLPQs);
            result.addAll(places);
        }

//        if (result.isEmpty()) {
//            System.out.println(getMapToDeleteWithCode().toString());
////            deleteWithCode();
//        }
        System.out.println("=============affectedPlaces");
        for (FeatureAnnotationToDelete entry : result) {
            System.out.println(entry.getMainFeatureLPQ() + " " + entry.getDocument() + " " + entry.getStartLine() + " " +entry.getEndLine() + " " + entry.getTangledFeatureLPQ() + " " + entry.getAnnotationType() );
        }
        return result;
    }

    public static Set<FeatureAnnotationToDelete> setElementsToDropWhenDeleting(FeatureModelFeature feature, List<String> featureTreeLPQs) {

        Project projectInstance = feature.getProject();

        FeatureFileMapping fileToAnnotation = FeatureLocationManager.getFeatureFileMapping(projectInstance, feature); // Feature -> FeatureLocations, each FeatureLocation -> FeatureBlocks
        ArrayList<FeatureAnnotationToDelete> baseFeatureLocations = getBaseFeatureLocations(fileToAnnotation, feature); // parentFeature, mapped File, start, end

        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(projectInstance); // Feature -> Features (tangled)
        HashSet<FeatureModelFeature> tangledFeatures = getTangledFeatures(tanglingMap, feature); // tangled features of specific Feature N

        ArrayList<FeatureAnnotationToDelete> tangledLocations = getTangledLocations(projectInstance, feature, tangledFeatures, fileToAnnotation, featureTreeLPQs);

        Set<FeatureAnnotationToDelete> affectedPlaces = processLocations(baseFeatureLocations, tangledLocations);
        System.out.println("=============affectedPlaces");
        for (FeatureAnnotationToDelete entry : affectedPlaces) {
            System.out.println(entry.getMainFeatureLPQ() + " " + entry.getDocument() + " " + entry.getStartLine() + " " +entry.getEndLine() + " " + entry.getTangledFeatureLPQ() + " " + entry.getAnnotationType() );
        }
        return affectedPlaces;

    }

    private static ArrayList<FeatureAnnotationToDelete> getTangledLocations(Project projectInstance, FeatureModelFeature feature, HashSet<FeatureModelFeature> tangledFeatures, FeatureFileMapping fileToAnnotation, List<String> featureTreeLPQs) {
        ArrayList<FeatureAnnotationToDelete> storeInterlockedLocations = new ArrayList<>();

        for (FeatureModelFeature tangled : tangledFeatures) { // for each feature tangled with A
            ArrayList<FeatureLocation> tangledLocations = FeatureLocationManager.getFeatureFileMapping(projectInstance, tangled).getFeatureLocations();
            for (FeatureLocation location : tangledLocations) {
                fileToAnnotation.getFeatureLocations().forEach(baseLocation -> {
                    if (baseLocation.getMappedPath().equals(location.getMappedPath())) {
                        for (FeatureLocationBlock fl : location.getFeatureLocations()) {
                            // if it's tangled with its own parent (or other feature in the parent tree, e.g. sibling) ignore
                            if (!featureTreeLPQs.contains(location.getMappedFeature().getLPQText())) {
                                FeatureAnnotationToDelete element = new FeatureAnnotationToDelete();

                                element.setTangledFeatureLPQ(location.getMappedFeature().getLPQText()); // 0: tangled feature
                                element.setFilePath(location.getMappedPath()); // 1: path to file with tangled feature

                                element.setStartLine(fl.getStartLine()); // 2: starting line of tangling
                                element.setEndLine(fl.getEndLine()); // 3: end line of tangling
                                element.setMainFeatureLPQ(feature.getLPQText()); // 4: feature it's tangled with
                                element.setAnnotationType(location.getAnnotationType()); // 5: annotation type of tangled annotation
                                storeInterlockedLocations.add(element);
                                System.out.println(element.getTangledFeatureLPQ() + " " + element.getFilePath() + " " + element.getStartLine() + " " + element.getEndLine() + " " + element.getAnnotationType()+ " " + element.getMainFeatureLPQ());
                            }
                        }

                    };
                });
            }
        }
        return storeInterlockedLocations;
    }

    private static HashSet<FeatureModelFeature> getTangledFeatures(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, FeatureModelFeature feature) {
        HashSet<FeatureModelFeature> tangledFeatures= new HashSet<>();

        for (FeatureModelFeature key : tanglingMap.keySet()) {
            if(key.getLPQText().equals(feature.getLPQText())) {
                tangledFeatures = tanglingMap.get(key);
            }
        }

        return tangledFeatures;
    }

    public static ArrayList<FeatureAnnotationToDelete> getBaseFeatureLocations(FeatureFileMapping fileToAnnotation, FeatureModelFeature parentFeature) {
        ArrayList<FeatureAnnotationToDelete> annotations = new ArrayList<>();

        for (FeatureLocation fm : fileToAnnotation.getFeatureLocations()) {

            for (FeatureLocationBlock block : fm.getFeatureLocations()) {

                FeatureAnnotationToDelete annotation = new FeatureAnnotationToDelete();
                annotation.setMainFeatureLPQ(parentFeature.getLPQText());
                annotation.setFilePath(fm.getMappedPath());
                annotation.setStartLine(block.getStartLine());
                annotation.setEndLine(block.getEndLine());

                annotations.add(annotation);
            }
        }
//        mapToDeleteWithCode.put(parentFeature, mapToDrop);

        return annotations;
    }

    private static Set<FeatureAnnotationToDelete> processLocations(ArrayList<FeatureAnnotationToDelete> baseLocations, ArrayList<FeatureAnnotationToDelete> tangledLocations) {
//        System.out.println("=============");
//        for (ArrayList<Object> entry : baseLocations) {
//            System.out.println(entry.get(0) + " " + entry.get(1)+ " " +entry.get(2)+ " " + entry.get(3));
//        }
//        System.out.println("=============");
//        for (ArrayList<Object> entry : tangledLocations) {
//            System.out.println(entry.get(0) + " " + entry.get(1)+ " " +entry.get(2)+ " " + entry.get(3));
//        }
        System.out.println("=============");
        for (FeatureAnnotationToDelete entry : baseLocations) {
            System.out.println(entry.getMainFeatureLPQ() + " " + entry.getFilePath() + " " + entry.getStartLine() + " " +entry.getEndLine() );
        }
        System.out.println("=============");
        for (FeatureAnnotationToDelete entry : tangledLocations) {
            System.out.println(entry.getMainFeatureLPQ() + " " + entry.getFilePath() + " " + entry.getStartLine() + " " +entry.getEndLine() );
        }
        Set<FeatureAnnotationToDelete> result = new HashSet<>();
        baseLocations.forEach(baseLocation -> {

            tangledLocations.forEach(tangledLocation -> {
                if (tangledLocation.getFilePath().equals(baseLocation.getFilePath())) {
                    FeatureAnnotationToDelete newElement = new FeatureAnnotationToDelete();
                    newElement.setTangledFeatureLPQ(tangledLocation.getTangledFeatureLPQ()); // 0: add name of tangled feature

//                    newElement.add(tangledLocation.get(1)); // add name of common file
                    VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(tangledLocation.getFilePath()); // add file
                    newElement.setDocument(FileDocumentManager.getInstance().getDocument(virtualFile)); // 1: document of annotation

                    ArrayList<Integer> rangeResult = getRange(baseLocation.getStartLine(), baseLocation.getEndLine(), tangledLocation.getStartLine(), tangledLocation.getEndLine());

                    if (rangeResult != null) {
                        newElement.setStartLine(rangeResult.get(0)); // 2: start lien
                        newElement.setEndLine(rangeResult.get(1)); // 3: end line
                        newElement.setMainFeatureLPQ(tangledLocation.getMainFeatureLPQ()); // 4: Main feature it's tangled with
                        newElement.setAnnotationType(tangledLocation.getAnnotationType()); // 5: annotation type
                        result.add(newElement);
                    }
                }
            });
        });

        return result;
    }

    private static ArrayList<Integer> getRange(int startA, int endA, int startB, int endB) {
        ArrayList<Integer> result = new ArrayList();
        Range rangeA = Range.between(startA, endA);
        Range rangeB = Range.between(startB, endB);

        if (rangeB.contains(startA) && rangeB.contains(endA)) {
            result.add(startB);
            result.add(endB);
            return result;
        }
        if (rangeA.contains(startB) && rangeA.contains(endB)) {
            result.add(startA);
            result.add(endA);
            return result;
        }
        if (rangeA.contains(startB)) {
            result.add(startA);
            result.add(endB);
            return result;
        }
        if (rangeB.contains(startA)) {
            result.add(startB);
            result.add(endA);
            return result;
        }
        return null;
    }

//    public static Set<ArrayList<Object>>  setElementsToDropWhenDeleting2(FeatureModelFeature feature) {
//
//        Project projectInstance = feature.getProject();
//
//        FeatureFileMapping fileToAnnotation = FeatureLocationManager.getFeatureFileMapping(projectInstance, feature);
//        ArrayList<ArrayList<Object>> baseFeaturelocations = new ArrayList();
//
//        for (FeatureLocation fm : fileToAnnotation.getFeatureLocations()) {
//            System.out.println("----------");
//            System.out.println(fm.getAnnotationType());
//            System.out.println(fm.getMappedFeature().getLPQText());
//            System.out.println(fm.getMappedPath());
//            System.out.println();
//            for (FeatureLocationBlock block : fm.getFeatureLocations()) {
//                ArrayList<Object> location = new ArrayList();
//                location.add(feature.getLPQText());
//                location.add(fm.getMappedPath());
//
//                location.add(block.getStartLine());
//                location.add(block.getEndLine());
//
//                baseFeaturelocations.add(location);
//                System.out.println("    " + block.getStartLine());
//                System.out.println("    " + block.getEndLine());
//            }
//        }
//        HashSet<FeatureModelFeature> tangledFeatures= new HashSet<>();
//        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(projectInstance);
//        for (FeatureModelFeature key : tanglingMap.keySet()) {
//            System.out.println("-----feature");
//            System.out.println(key.getLPQText());
//            System.out.println("    tangled features");
//            for (FeatureModelFeature value : tanglingMap.get(key)) {
//                System.out.println(value.getLPQText());
//            }
//
//            if(key.getLPQText().equals(feature.getLPQText())) {
//                tangledFeatures = tanglingMap.get(key);
//            }
//        }
//
//        ArrayList<ArrayList<Object>> storeInterlockedLocations = new ArrayList<>();
//
//        for (FeatureModelFeature tangled : tangledFeatures) { // for each feature tangled with A
//            ArrayList<FeatureLocation> tangledLocations = FeatureLocationManager.getFeatureFileMapping(projectInstance, tangled).getFeatureLocations();
//            for (FeatureLocation location : tangledLocations) {
//                fileToAnnotation.getFeatureLocations().forEach(baseLocation -> {
//                    if (baseLocation.getMappedPath().equals(location.getMappedPath())) {
//
//                        for (FeatureLocationBlock fl : location.getFeatureLocations()) {
//                            ArrayList element = new ArrayList() {};
//                            element.add(location.getMappedFeature().getLPQText());
//                            element.add(location.getMappedPath());
//
//                            element.add(fl.getStartLine());
//                            element.add(fl.getEndLine());
//                            storeInterlockedLocations.add(element);
//                            System.out.println(location.getMappedFeature().getLPQText() + " " + location.getMappedPath() + " " + element.get(2) + " " + element.get(3));
//                        }
//
//                    };
//                });
//            }
//        }
//
//        Set<ArrayList<Object>> affectedPlaces = processLocations(baseFeaturelocations, storeInterlockedLocations);
//        System.out.println("=============affectedPlaces");
//        for (ArrayList<Object> entry : affectedPlaces) {
////            VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath((String) entry.get(1));
////            System.out.println(virtualFile);
////            if (virtualFile != null) {
////                System.out.println();
////                entry.set(1, FileDocumentManager.getInstance().getDocument(virtualFile));
////            }
//            System.out.println(entry.get(0) + " " + entry.get(1)+ " " +entry.get(2)+ " " + entry.get(3));
//        }
//        return affectedPlaces;

//        Document document =
//                PsiDocumentManager.getInstance(projectInstance).getDocument(feature.getContainingFile());
//
//        EditorTextField editorTextField = new EditorTextField(document, projectInstance, feature.getContainingFile().getFileType());
//
//        CodeEditorModal cem = new CodeEditorModal(projectInstance);
//        cem.show();

//        JDialog contentPanel = new JDialog();
////        contentPanel.setLocationRelativeTo(getParentWindow);
//        JComponent component = editorTextField.getComponent();
//        contentPanel.add(component);
//        component.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
//        component.setVisible(true);
//        contentPanel.setVisible(true);
//        scrollPaneCode.setViewportView(component);

// =========== =============== =============== ===============
//        AtomicReference<String> errorText = new AtomicReference<>("Deleting feature might affect other tangled features: ");
//        if (!tangledFeatures.isEmpty()) {
//            tangledFeatures.forEach(f -> errorText.set(errorText.get() + f.getLPQText() + ","));
//            errorText.set(errorText.get().substring(0,errorText.get().length()-1));
//            return;
//        }
// =========== =============== =============== ===============


//        Map<String, List<Object>> fileToAnnotation = getListOfFileToAnnotationsOfFeature(projectInstance, feature);
//        for (Map.Entry<String, List<Object>> test : fileToAnnotation.entrySet()) {
//            System.out.println(test.getKey());
//            for (Object test2 : test.getValue()) {
//                System.out.println(test2.toString());
//            }
//        }

//    }

    // returns
    // filename (String)
    // -> list of line numbers of annotations of a given feature (List<Object>)
    private static Map<String, List<Object>> getListOfFileToAnnotationsOfFeature(Project projectInstance, FeatureModelFeature feature) {
        Map<String, List<Object>> fileToAnnotation = new HashMap<>();

        for (PsiReference reference : ReferencesSearch.search(feature)) {

            InjectedLanguageManager injManager = InjectedLanguageManager.getInstance(projectInstance);

            PsiLanguageInjectionHost host = injManager.getInjectionHost(reference.getElement());
            if (host != null) {

                Document document = PsiDocumentManager.getInstance(projectInstance).getDocument(host.getContainingFile());
                String filePath = host.getContainingFile().getVirtualFile().getPath();

                AtomicReference<Map.Entry<String, List<Object>>> entry = new AtomicReference<>();
                fileToAnnotation.entrySet().stream().forEach(entryVal ->
                {
                    if(entryVal.getKey().equals(filePath)){
                        entry.set(entryVal);
                    }
                });
                if (entry.get() == null) {
                    entry.set(Map.entry(filePath, new ArrayList<>()));
                    fileToAnnotation.put(entry.get().getKey(), entry.get().getValue());
                }

                List<Object> commentAndLine = new ArrayList<>();
                String comment = host.getText();
                int lineNum = document.getLineNumber(host.getTextOffset()) ;
                commentAndLine.add(comment);
                commentAndLine.add(lineNum);
                entry.get().getValue().add(commentAndLine);

                System.out.println(host.getText());
                System.out.println(host.getTextOffset());
                System.out.println(document.getLineNumber(host.getTextOffset()));
            }

        }
        return fileToAnnotation;
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
        System.out.println("-----map to rename------");
        toRename.forEach((key, value) -> {
            System.out.println(key.getLPQText());
            System.out.println(Arrays.toString(value.toArray()));
        });
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
