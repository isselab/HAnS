/*
Copyright 2024 David Stechow & Philipp Kusmierz

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

package se.isselab.HAnS.featureLocation;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import se.isselab.HAnS.FeatureAnnotationSearchScope;
import se.isselab.HAnS.codeAnnotation.psi.*;
import se.isselab.HAnS.featureExtension.backgroundTask.BackgroundTask;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;


import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileAnnotation;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFileReferences;
import se.isselab.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.isselab.HAnS.referencing.FileReferenceUtil;

import java.util.HashMap;


public class FeatureLocationManager {

    private FeatureLocationManager() {

    }

    /**
     * Method to get all {@link FeatureFileMapping} for a given project.
     * Represented as a Map of a Feature-LPQ to its corresponding FileMapping
     * Includes expensive <code>ReferencesSearch.search()</code>, which might cause UI freezes depending on the size of the search.
     * Better use BackgroundTask.
     *
     * @param project The project
     * @return Map of all FeatureFileMappings for the given project
     * @see FeatureFileMapping
     */
    public static HashMap<String, FeatureFileMapping> getAllFeatureFileMappings(Project project) {
        HashMap<String, FeatureFileMapping> mapping = new HashMap<>();
        for (var feature : ReadAction.compute(() -> FeatureModelUtil.findFeatures(project))) {
            mapping.put(ReadAction.compute(feature::getLPQText), FeatureLocationManager.getFeatureFileMapping(project, feature));
        }

        return mapping;
    }
    // &begin[FeatureFileMapping]

    /**
     * Returns the FeatureFileMapping for the given feature.
     * Includes expensive ReferencesSearch.search(), which might cause UI freezes depending on the size of the search.
     * Better use BackgroundTask.
     *
     * @param feature Corresponding feature for which the FileMapping should be calculated
     * @return FeatureFileMapping for the given feature
     * @see BackgroundTask
     */
    public static FeatureFileMapping getFeatureFileMapping(Project project, FeatureModelFeature feature) {
        FeatureFileMapping featureFileMapping = new FeatureFileMapping(feature);
        Query<PsiReference> featureReference = ReferencesSearch.search(feature, FeatureAnnotationSearchScope.projectScope(project), true);
        for (PsiReference reference : ReadAction.compute(() -> featureReference)) {
            //get comment sibling of the feature comment

            PsiElement element = reference.getElement();

            //determine file type and process content
            var fileType = ReadAction.compute(element::getContainingFile);
            if (fileType instanceof CodeAnnotationFile) {
                processCodeFile(project, featureFileMapping, element);
            } else if (fileType instanceof FileAnnotationFile) {
                processFeatureToFile(project, featureFileMapping, element);
            } else if (fileType instanceof FolderAnnotationFile) {
                PsiDirectory dir = ReadAction.compute(fileType::getContainingDirectory);
                if (dir == null)
                    continue;
                processFeatureToFolder(project, featureFileMapping, dir);

            }

        }
        featureFileMapping.buildFromQueue();
        return featureFileMapping;
    }
    // &end[FeatureFileMapping]

    private static void processCodeFile(Project project, FeatureFileMapping featureFileMapping, PsiElement element) {
        var commentElement = ReadAction.compute(() -> PsiTreeUtil.getContextOfType(element, PsiComment.class));

        // var commentElement = PsiTreeUtil.getContextOfType(element, PsiComment.class);

        if (commentElement == null) {
            return;
        }

        var featureMarker = ReadAction.compute(() -> element.getParent().getParent());
        FeatureFileMapping.MarkerType type;

        //get feature type
        if (featureMarker instanceof CodeAnnotationBeginmarker)
            type = FeatureFileMapping.MarkerType.begin;
        else if (featureMarker instanceof CodeAnnotationEndmarker)
            type = FeatureFileMapping.MarkerType.end;
        else if (featureMarker instanceof CodeAnnotationLinemarker)
            type = FeatureFileMapping.MarkerType.line;
        else
            type = FeatureFileMapping.MarkerType.none;

        var file = ReadAction.compute(() -> commentElement.getContainingFile().getVirtualFile());
        if (file == null)
            return;
        featureFileMapping.enqueue(ReadAction.compute(file::getPath), ReadAction.compute(() -> getLine(project, commentElement)), type, FeatureFileMapping.AnnotationType.code);
    }

    private static void processFeatureToFile(Project project, FeatureFileMapping featureFileMapping, PsiElement element) {

        var parent = ReadAction.compute(() -> PsiTreeUtil.getParentOfType(element, FileAnnotationFileAnnotation.class));
        if (parent == null)
            return;

        var fileReferences = PsiTreeUtil.getChildrenOfType(parent, FileAnnotationFileReferences.class);
        if (fileReferences == null)
            return;

        for (var ref : fileReferences) {
            //get name of file
            for (var file : ref.getFileReferenceList()) {

                PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);

                var fileName = ReadAction.compute(() -> FileReferenceUtil.findFile(file, file.getFileName().getText()));
                if (fileName.isEmpty())
                    continue;
                var psiFile = fileName.get(0);

                Document document = ReadAction.compute(() -> psiDocumentManager.getDocument(psiFile));
                if (document == null)
                    return;

                featureFileMapping.enqueue(psiFile.getVirtualFile().getPath(), document.getLineCount() > 0 ? document.getLineCount() - 1 : 0, FeatureFileMapping.MarkerType.none, FeatureFileMapping.AnnotationType.file);
            }
        }
    }

    private static void processFeatureToFolder(Project project, FeatureFileMapping featureFileMapping, PsiDirectory directory) {

        for (var file : ReadAction.compute(directory::getFiles)) {
            //skip .feature-to-folder and .feature-to-file files
            if (file instanceof FolderAnnotationFile || file instanceof FileAnnotationFile)
                continue;

            PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
            Document document = ReadAction.compute(() -> psiDocumentManager.getDocument(file));
            if (document == null)
                return;

            featureFileMapping.enqueue(file.getVirtualFile().getPath(), document.getLineCount() > 0 ? document.getLineCount() - 1 : 0, FeatureFileMapping.MarkerType.none, FeatureFileMapping.AnnotationType.file);
        }
        for (var dir : ReadAction.compute(directory::getSubdirectories)) {
            //recursively add subdirectories to the feature
            processFeatureToFolder(project,
                    featureFileMapping, dir);
        }
    }

    private static int getLine(Project project, PsiElement elem) {

        PsiDocumentManager psiDocumentManager = PsiDocumentManager.getInstance(project);
        PsiFile openedFile = ReadAction.compute(elem::getContainingFile);

        //iterate over each psiElement and check for PsiComment-Feature-Annotations
        if (openedFile == null)
            return -1;
        Document document = psiDocumentManager.getDocument(openedFile);
        if (document == null)
            return -1;

        return document.getLineNumber(elem.getTextRange().getStartOffset());
    }
}
