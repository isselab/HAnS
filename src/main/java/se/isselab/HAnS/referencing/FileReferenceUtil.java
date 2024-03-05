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

import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.fileAnnotation.FileAnnotationFileType;
import se.isselab.HAnS.folderAnnotation.FolderAnnotationFileType;

import java.util.ArrayList;
import java.util.List;

public class FileReferenceUtil {

    public static List<PsiFile> findFile(PsiElement element, String file) {
        List<PsiFile> result = new ArrayList<>();
        List<PsiFile> l = findFiles(element);
        for (PsiFile f : l) {
            if (!f.isDirectory()
                    && f.getFileType() != FileAnnotationFileType.INSTANCE
                    && f.getFileType() != FolderAnnotationFileType.INSTANCE
                    && f.getName().equals(file)) {
                result.add(f);
            }
        }
        return result;
    }

    public static List<PsiFile> findFiles(PsiElement element) {
        List<PsiFile> result = new ArrayList<>();
        PsiDirectory d = element.getContainingFile().getContainingDirectory();
        PsiFile @NotNull [] l = d.getFiles();
        for (PsiFile f : l) {
            if (!f.isDirectory()
                    && f.getFileType() != FileAnnotationFileType.INSTANCE
                    && f.getFileType() != FolderAnnotationFileType.INSTANCE) {
                result.add(f);
            }
        }

        return result;
    }

}
