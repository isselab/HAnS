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
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Range;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.FeatureAnnotationToDelete;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReferences;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class FeatureReferenceUtil {

    private static String lpq = null;
    private static PsiElement origin = null;
    private static boolean addingOrDeleting = false;

    private static Map<FeatureModelFeature, List<PsiReference>> mapToRename = new HashMap<>();
    private static Map<FeatureModelFeature, List<PsiReference>> mapToRemove = new HashMap<>();
    private static final Map<FeatureModelFeature, ArrayList<Object>> mapToDeleteWithCode = new HashMap<>();
    private static Map<String, List<PsiReference>> mapToRenameWhenAdding = new HashMap<>();
    private static final Map<Document, Set<Integer>> mapToDeleteAfterDeletingWithCode = new HashMap<>(); // stores document->empty lines to remove after renaming, because otherwise it would corrupt the document

    private FeatureReferenceUtil() {}

    public static String getLPQ(FeatureModelFeature feature, String newName) {
        if (lpq == null) {
            return setLPQ(feature, newName);
        } else {
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
        mapToRename.clear();
        mapToRemove.clear();
        mapToDeleteWithCode.clear();
        mapToRenameWhenAdding.clear();
        mapToDeleteAfterDeletingWithCode.clear();
    }

    public static Map<Document, Set<Integer>> getMapToDeleteAfterDeletingWithCode() {
        return mapToDeleteAfterDeletingWithCode;
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

    public static Map<FeatureModelFeature, ArrayList<Object>> getMapToDeleteWithCode() {
        return mapToDeleteWithCode;
    }

    private static String setLPQ(FeatureModelFeature feature, String newName) {
        PsiElement fileCopy = feature.getContainingFile().copy();
        final FeatureModelFeature[] e = new FeatureModelFeature[1];
        fileCopy.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element instanceof FeatureModelFeature featureModelFeature &&
                        featureModelFeature.getLPQText().equals(feature.getLPQText())) {
                    e[0] = featureModelFeature;
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
        origin = feature;
        lpq = e[0].getLPQText();
        return lpq;
    }

    public static void rename() {
        Map<FeatureModelFeature, List<PsiReference>> toRename = getElementsToRename();

        for (Map.Entry<FeatureModelFeature, List<PsiReference>> entry : toRename.entrySet()) {
            String newLPQ = entry.getKey().getLPQText();
            for (PsiReference reference : entry.getValue()) {
                reference.handleElementRename(newLPQ);
            }
        }
    }

    public static void setElementsToRenameWhenRenaming(FeatureModelFeature element, String newName) {
        Map<FeatureModelFeature, List<PsiReference>> toRename = new HashMap<>();
        List<FeatureModelFeature> elementsToRename = getElementsToRenameWhenRenaming(element, newName);

        mapElementsToRename(toRename, elementsToRename);
    }

    private static List<FeatureModelFeature> getElementsToRenameWhenRenaming(FeatureModelFeature element, String newName) {
        List<FeatureModelFeature> elementsToRename = new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature featureModelFeature &&
                        (featureModelFeature.getLPQText().contains(newName) ||
                                featureModelFeature.getLPQText().contains(Objects.requireNonNull(element.getNode()
                                        .findChildByType(FeatureModelTypes.FEATURENAME)).getText()))) {
                    elementsToRename.add(featureModelFeature);
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
        List<FeatureModelFeature> elementsToRename = new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature featureModelFeature && (featureModelFeature).getLPQText().contains(newName)) {
                    elementsToRename.add(featureModelFeature);
                }
                super.visitElement(e);
            }
        });
        return elementsToRename;
    }

    public static void delete() {
        Map<FeatureModelFeature, List<PsiReference>> toDelete = getElementsToRemove();

        for (Map.Entry<FeatureModelFeature, List<PsiReference>> entry : toDelete.entrySet()) {

            for (PsiReference reference : entry.getValue()) {
                InjectedLanguageManager injManager = InjectedLanguageManager.getInstance(entry.getKey().getProject());

                PsiLanguageInjectionHost host = injManager.getInjectionHost(reference.getElement());
                if (host != null) { // if comment annotation
                    AtomicReference<Document> doc = new AtomicReference<>();
                    if (reference.getElement().getParent().getChildren().length > 1) { // if annotation has multiple features e.g. begin[FeatureA, FeatureB]
                        Runnable r = () -> {
                            PsiElement parent = reference.getElement().getParent();
                            String parentPsi = parent.getText(); // e.g. [A, B ,  C]
                            String[] array = parentPsi.replaceAll("\\[|]|\\s", "").split(","); // transform to list of features A, B, C

                            ArrayList<String> featuresArr = new ArrayList<>(Arrays.asList(array)); // transform to arraylist
                            featuresArr.remove(reference.getElement().getText()); // remove our element from arraylist
                            String[] resultFeatureArr = featuresArr.toArray(new String[0]); // back to List

                            PsiFile file = reference.getElement().getContainingFile();

                            doc.set(FileDocumentManager.getInstance().getDocument(file.getVirtualFile()));
                            int startLine = parent.getTextOffset(); // get start/end offsets of parent brackets [A, B ,  C]
                            int endLine = parent.getTextOffset() + parent.getTextLength();

                            doc.get().replaceString(startLine, endLine, Arrays.toString(resultFeatureArr)); // replace parent with new annotated feature list without our feature
                        };
                        WriteCommandAction.runWriteCommandAction(entry.getKey().getProject(), r);
                        PsiDocumentManager.getInstance(entry.getKey().getProject()).commitDocument(doc.get());

                    } else if (reference.getElement().getParent().getChildren().length == 1) {
                        // otherwise delete whole comment (annotation)
                        Runnable r = host::delete;
                        WriteCommandAction.runWriteCommandAction(entry.getKey().getProject(), r);
                    }

                } else { // if feature to folder/file annotation
                    deleteFileFolderAnnotation(entry.getKey().getProject(), reference);
                }
            }
        }
    }

    private static void deleteFileFolderAnnotation(Project project, PsiReference reference) {
        if (reference.getElement().getNextSibling() != null) {
            WriteCommandAction.runWriteCommandAction(project, getRunnableForNextSibling(reference));
        }

        if (reference.getElement().getPrevSibling() != null && reference.getElement().getNextSibling() == null) {
            Runnable r = () -> {
                PsiElement prevChar = reference.getElement().getPrevSibling();
                if (prevChar.getText().equals(",")) {
                    prevChar.delete();
                }
                if (prevChar.getText().equals(" ")) {
                    if (prevChar.getPrevSibling() != null) {
                        PsiElement prevPrevChar = prevChar.getPrevSibling();
                        if (prevPrevChar.getText().equals(",")) {
                            prevChar.delete();
                            prevPrevChar.delete();
                        }
                    } else {
                        prevChar.delete();
                    }
                }
            };
            WriteCommandAction.runWriteCommandAction(project, r);
        }

        if (reference.getElement().getPrevSibling() == null && reference.getElement().getNextSibling() == null &&
                reference.getElement().getParent().getPrevSibling() != null &&
                reference.getElement().getParent().getPrevSibling().getPrevSibling() instanceof FileAnnotationFileReferences) { // get file list
            Runnable r = () ->
                    reference.getElement().getParent().getPrevSibling().getPrevSibling().delete(); // delete all files
            WriteCommandAction.runWriteCommandAction(project, r);
        }


        Runnable r = () -> reference.getElement().delete(); // remove element
        WriteCommandAction.runWriteCommandAction(project, r);
    }

    private static Runnable getRunnableForNextSibling(PsiReference reference) {
        PsiElement nextChar = reference.getElement().getNextSibling();
        return () -> {
            if (nextChar.getText().equals(" ")) {
                nextChar.delete(); // remove space after element
            }
            if (nextChar.getText().equals(",")) {
                if (nextChar.getNextSibling() != null) {
                    PsiElement nextNextChar = nextChar.getNextSibling();
                    if (nextNextChar.getText().equals(" ")) {
                        nextNextChar.delete();
                    }
                }
                reference.getElement().getNextSibling().delete();
            }
        };
    }

    public static void deleteSpacesAfterDeleteWithCode(Project project) {
        Map<Document, Set<Integer>> documentToLines = getMapToDeleteAfterDeletingWithCode();
        for (Map.Entry<Document, Set<Integer>> entry : documentToLines.entrySet()) {
            Document document = entry.getKey();
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
            Runnable r = () -> {
                document.setReadOnly(false);
                document.setText(getNewDocumentText(entry, document));
            };
            WriteCommandAction.runWriteCommandAction(project, r);
        }

    }

    private static @NotNull String getNewDocumentText(Map.Entry<Document, Set<Integer>> entry, Document document) {
        Set<Integer> linesToExclude = entry.getValue();
        String newDocumentText = "";
        String documentText = document.getText();
        for (int i = 0; i < document.getLineCount(); i++) {
            if (linesToExclude.contains(i)) {
                continue;
            }
            String line = documentText.substring(document.getLineStartOffset(i), document.getLineEndOffset(i)).concat("\n");
            newDocumentText = newDocumentText.concat(line);
        }
        return newDocumentText;
    }

    public static void deleteWithCode() {
        Map<FeatureModelFeature, ArrayList<Object>> featureToAnnotations = getMapToDeleteWithCode();
        // feature -> list of either PsiReference or FeatureAnnotationToDelete
        for (Map.Entry<FeatureModelFeature, ArrayList<Object>> entry : featureToAnnotations.entrySet()) {
            FeatureModelFeature feature = entry.getKey();
            ArrayList<Object> annotations = entry.getValue();
            Project projectInstance = feature.getProject();
            Map<Document, Set<Integer>> codeAnnotations = (Map<Document, Set<Integer>>) annotations.get(0);
            List<PsiReference> fileFolderAnnotation = (List<PsiReference>) annotations.get(1);
            codeAnnotations.forEach((document, linesToExclude) -> {

                mapToDeleteAfterDeletingWithCode.put(document, linesToExclude);

                for (int i = 0; i < document.getLineCount(); i++) {
                    if (linesToExclude.contains(i)) {
                        int iFinal = i;
                        Runnable r = () -> document.deleteString(document.getLineStartOffset(iFinal), document.getLineEndOffset(iFinal));
                        WriteCommandAction.runWriteCommandAction(projectInstance, r);
                    }
                }
            });

            fileFolderAnnotation.forEach(annotation -> deleteFileFolderAnnotation(projectInstance, annotation));
        }
    }

    public static void setMapToDeleteWithCode(FeatureModelFeature feature) {
        List<PsiElement> elementsToDelete = new ArrayList<>();
        traverseFeatureWithChildren(feature, elementsToDelete, new ArrayList<>());

        Project projectInstance = feature.getProject();
        elementsToDelete.forEach(child -> {
            FeatureModelFeature childFeature = (FeatureModelFeature) child;

            FeatureFileMapping fileToAnnotation = FeatureLocationManager.getFeatureFileMapping(projectInstance, childFeature);
            Map<Document, Set<Integer>> codeAnnotations = getCodeAnnotations(fileToAnnotation); // code Annotations

            List<PsiReference> fileAndFolderAnnotations = new ArrayList<>();
            for (PsiReference reference : ReferencesSearch.search(child)) {
                InjectedLanguageManager injManager = InjectedLanguageManager.getInstance(projectInstance);

                PsiLanguageInjectionHost host = injManager.getInjectionHost(reference.getElement());
                if (host == null) { // if it's file or folder annotation, so not an injection
                    fileAndFolderAnnotations.add(reference);
                }
            }

            mapToDeleteWithCode.put(childFeature, new ArrayList<>());
            mapToDeleteWithCode.get(childFeature).add(codeAnnotations);
            mapToDeleteWithCode.get(childFeature).add(fileAndFolderAnnotations);
        });
    }

    private static Map<Document, Set<Integer>> getCodeAnnotations(FeatureFileMapping fileToAnnotation) {
        Map<Pair<String, String>, Set<Integer>> mapToDrop = new HashMap<>(); // documentName -> ranges of annotation blocks

        for (FeatureLocation fm : fileToAnnotation.getFeatureLocations()) {
            fm.getFeatureLocations().forEach(block -> {
                if (fm.getAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE)) {

                    Set<Integer> entry = mapToDrop.get(fm.getMappedPathPairMappedBy());

                    if (entry == null) {
                        entry = new HashSet<>();
                        for (int i = block.getStartLine(); i < block.getEndLine() + 1; i++) {
                            entry.add(i);
                        }
                        mapToDrop.put(fm.getMappedPathPairMappedBy(), entry);
                    } else {
                        for (int i = block.getStartLine(); i < block.getEndLine() + 1; i++) {
                            entry.add(i);
                        }
                    }
                }
            });
        }

        // substitute string to document
        return mapToDrop.entrySet().stream()
                .collect(Collectors.toMap(entry -> {
                    VirtualFile file = LocalFileSystem.getInstance().findFileByPath(entry.getKey().first);
                    if (file != null) {
                        return FileDocumentManager.getInstance().getDocument(file);
                    }
                    return null;
                }, entry -> entry.getValue().stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new))));
    }

    public static Set<FeatureAnnotationToDelete> setElementsToDropWhenDeleting(FeatureModelFeature parentFeature) {
        List<PsiElement> featureWithKids = new ArrayList<>();
        List<String> featureWithKidsLPQs = new ArrayList<>();
        traverseFeatureWithChildren(parentFeature, featureWithKids, featureWithKidsLPQs);
        Set<FeatureAnnotationToDelete> result = new HashSet<>();
        for (PsiElement feature : featureWithKids) {
            Set<FeatureAnnotationToDelete> places = getElementsToDropWhenDeleting((FeatureModelFeature) feature, featureWithKidsLPQs);
            result.addAll(places);
        }

        return result;
    }

    public static Set<FeatureAnnotationToDelete> getElementsToDropWhenDeleting(FeatureModelFeature feature, List<String> featureTreeLPQs) {

        Project projectInstance = feature.getProject();

        FeatureFileMapping fileToAnnotation = FeatureLocationManager.getFeatureFileMapping(projectInstance, feature); // Feature -> FeatureLocations, each FeatureLocation -> FeatureBlocks
        ArrayList<FeatureAnnotationToDelete> baseFeatureLocations = getFeatureAnnotationLocations(fileToAnnotation, feature); // parentFeature, mapped File, start, end

        HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(projectInstance); // Feature -> Features (tangled)
        HashSet<FeatureModelFeature> tangledFeatures = getTangledFeatures(tanglingMap, feature); // tangled features of specific Feature N

        ArrayList<FeatureAnnotationToDelete> tangledLocations = getTangledLocations(projectInstance, feature, tangledFeatures, fileToAnnotation, featureTreeLPQs);

        return processLocations(baseFeatureLocations, tangledLocations);
    }

    // takes main feature locations and tangled feature locations
    // if file paths of both annotations are the same, creates FeatureAnnotationToDelete instance
    // that contains information about both main and tangled feature, such as
    // mainLpq, tangledLpq, file, start/end line (if code annotation) and annotation type
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
                                FeatureAnnotationToDelete element = getFeatureAnnotationToDelete(feature, location, fl);
                                storeInterlockedLocations.add(element);
                            }
                        }

                    }
                });
            }
        }
        return storeInterlockedLocations;
    }

    private static @NotNull FeatureAnnotationToDelete getFeatureAnnotationToDelete(FeatureModelFeature feature, FeatureLocation location, FeatureLocationBlock fl) {
        FeatureAnnotationToDelete element = new FeatureAnnotationToDelete();

        element.setTangledFeatureLPQ(location.getMappedFeature().getLPQText()); // tangled feature
        element.setFilePath(location.getMappedPath()); // path to file with tangled feature

        element.setStartLine(fl.getStartLine()); // starting line of tangling
        element.setEndLine(fl.getEndLine()); // end line of tangling
        element.setMainFeatureLPQ(feature.getLPQText()); // feature it's tangled with
        element.setTangledAnnotationType(location.getAnnotationType()); // annotation type of tangled annotation
        return element;
    }

    // returns set of all features tangled with FeatureModelFeature feature
    private static HashSet<FeatureModelFeature> getTangledFeatures(HashMap<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap, FeatureModelFeature feature) {
        HashSet<FeatureModelFeature> tangledFeatures = new HashSet<>();

        for (var entry : tanglingMap.entrySet()) {
            if (entry.getKey().getLPQText().equals(feature.getLPQText())) {
                tangledFeatures = entry.getValue();
            }
        }

        return tangledFeatures;
    }

    // get list of all code annotation blocks of certain feature
    // stores FeatureLpq, FilePath, start line, end line and annotation type
    private static ArrayList<FeatureAnnotationToDelete> getFeatureAnnotationLocations(FeatureFileMapping fileToAnnotation, FeatureModelFeature parentFeature) {
        ArrayList<FeatureAnnotationToDelete> annotations = new ArrayList<>();

        for (FeatureLocation fm : fileToAnnotation.getFeatureLocations()) {

            for (FeatureLocationBlock block : fm.getFeatureLocations()) {

                FeatureAnnotationToDelete annotation = new FeatureAnnotationToDelete();
                annotation.setMainFeatureLPQ(parentFeature.getLPQText());
                annotation.setFilePath(fm.getMappedPath());
                annotation.setStartLine(block.getStartLine());
                annotation.setEndLine(block.getEndLine());
                annotation.setMainAnnotationType(fm.getAnnotationType());

                annotations.add(annotation);
            }
        }

        return annotations;
    }

    // takes two FeatureAnnotationToDelete instances and produces one FeatureAnnotationToDelete
    private static Set<FeatureAnnotationToDelete> processLocations(ArrayList<FeatureAnnotationToDelete> baseLocations, ArrayList<FeatureAnnotationToDelete> tangledLocations) {
        Set<FeatureAnnotationToDelete> result = new HashSet<>();
        baseLocations.forEach(baseLocation -> tangledLocations.forEach(tangledLocation -> {
            if (tangledLocation.getFilePath().equals(baseLocation.getFilePath())) {
                FeatureAnnotationToDelete newElement = new FeatureAnnotationToDelete();
                newElement.setTangledFeatureLPQ(tangledLocation.getTangledFeatureLPQ()); // 0: add name of tangled feature

                VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(tangledLocation.getFilePath()); // add file
                if (virtualFile != null) {
                    newElement.setDocument(FileDocumentManager.getInstance().getDocument(virtualFile)); // 1: document of annotation
                }

                newElement.setMainFeatureLPQ(tangledLocation.getMainFeatureLPQ()); // 4: Main feature it's tangled with
                newElement.setTangledAnnotationType(tangledLocation.getTangledAnnotationType()); // 5: tangled feature annotation type
                newElement.setMainAnnotationType(baseLocation.getMainAnnotationType()); // 6: main annotation type

                if (tangledLocation.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE) &&
                        baseLocation.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE)) {
                    ArrayList<Integer> rangeResult = getRange(baseLocation.getStartLine(), baseLocation.getEndLine(), tangledLocation.getStartLine(), tangledLocation.getEndLine());
                    if (rangeResult != null) {
                        newElement.setStartLine(rangeResult.get(0)); // 2: start line
                        newElement.setEndLine(rangeResult.get(1)); // 3: end line
                        result.add(newElement);
                    }
                } else if (tangledLocation.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE) &&
                        !baseLocation.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE)) {
                    newElement.setStartLine(tangledLocation.getStartLine()); // 2: start line
                    newElement.setEndLine(tangledLocation.getEndLine()); // 3: end line
                    result.add(newElement);
                } else if (!tangledLocation.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE) &&
                        baseLocation.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.CODE)) {
                    newElement.setStartLine(baseLocation.getStartLine()); // 2: start line
                    newElement.setEndLine(baseLocation.getEndLine()); // 3: end line
                    result.add(newElement);
                } else {
                    newElement.setStartLine(0); // 2: start line
                    newElement.setEndLine(0); // 3: end line
                    result.add(newElement);
                }

            }
        }));

        return result;
    }

    // if two features are tangled using code annotation,
    // finds line range that includes both FeatureA and FeatureB
    private static @Nullable ArrayList<Integer> getRange(int startA, int endA, int startB, int endB) {
        ArrayList<Integer> result = new ArrayList<>();
        Range<Integer> rangeA = new Range<>(startA, endA);
        Range<Integer> rangeB = new Range<>(startB, endB);

        if (rangeB.isWithin(startA, true) && rangeB.isWithin(endA, true)) {
            result.add(startB);
            result.add(endB);
            return result;
        }
        if (rangeA.isWithin(startB, true) && rangeA.isWithin(endB, true)) {
            result.add(startA);
            result.add(endA);
            return result;
        }
        if (rangeA.isWithin(startB, true)) {
            result.add(startA);
            result.add(endB);
            return result;
        }
        if (rangeB.isWithin(startA, true)) {
            result.add(startB);
            result.add(endA);
            return result;
        }
        return null;
    }

    public static void setElementsToDelete(FeatureModelFeature element) {
        Map<FeatureModelFeature, List<PsiReference>> toDelete = new HashMap<>();

        List<PsiElement> elementsToDelete = new ArrayList<>();
        traverseFeatureWithChildren(element, elementsToDelete, new ArrayList<>());
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
        addingOrDeleting = true;

        Map<FeatureModelFeature, List<PsiReference>> toRename = new HashMap<>();

        List<PsiElement> elementsToDelete = new ArrayList<>();
        List<String> lpqToDelete = new ArrayList<>();
        traverseFeatureWithChildren(element, elementsToDelete, lpqToDelete);

        List<FeatureModelFeature> elementsToRename = new ArrayList<>();
        for (PsiElement feature : elementsToDelete) {
            elementsToRename.addAll(getElementsToRenameWhenDeleting(((FeatureModelFeature) feature), lpqToDelete));
        }

        mapElementsToRename(toRename, elementsToRename);
    }

    private static void mapElementsToRename(Map<FeatureModelFeature, List<PsiReference>> toRename, List<FeatureModelFeature> elementsToRename) {
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
        List<FeatureModelFeature> elementsToRename = new ArrayList<>();
        element.getContainingFile().accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement e) {
                if (e instanceof FeatureModelFeature featureModelFeature &&
                        featureModelFeature.getLPQText().contains(element.getFeatureName()) &&
                        !(lpqToRemove.contains(featureModelFeature.getLPQText()))) {
                    elementsToRename.add(featureModelFeature);
                }

                super.visitElement(e);
            }
        });
        return elementsToRename;
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

                    for (PsiReference reference : entry.getValue()) {
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

        List<PsiElement> elementsToUpdate = getElementsToRenameAfterAddingWithChildren(element);
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
        List<PsiElement> elementsToUpdate = new ArrayList<>();
        List<String> lpqToDelete = new ArrayList<>();

        traverseFeatureWithChildren(element, elementsToUpdate, lpqToDelete);
        return elementsToUpdate;
    }

}
