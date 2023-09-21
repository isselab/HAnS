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
package se.isselab.HAnS.fileAnnotation.psi;

import com.google.common.collect.Iterables;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.fileAnnotation.FileAnnotationFileType;

import java.util.Collection;

public class FileAnnotationElementFactory {

    public static FileAnnotationLpq createLPQ(Project project, String name) {
        final FileAnnotationFile file = createLPQFile(project, name);
        @NotNull Collection<FileAnnotationLpq> lpq = PsiTreeUtil.collectElementsOfType(file, FileAnnotationLpq.class);
        return Iterables.get(lpq, 0);
    }

    public static FileAnnotationFile createLPQFile(Project project, String text) {
        String name = "dummy.feature-to-file";
        return (FileAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FileAnnotationFileType.INSTANCE, "dummy.file\n" + text);
    }

    public static FileAnnotationFileReference createFileReference(Project project, String name) {
        final FileAnnotationFile file = createFileReferenceFile(project, name);
        return file.findChildByClass(FileAnnotationFileReference.class);
    }

    public static FileAnnotationFile createFileReferenceFile(Project project, String text) {
        String name = "dummy.feature-to-file";
        return (FileAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FileAnnotationFileType.INSTANCE, text + "\ndummy");
    }
}