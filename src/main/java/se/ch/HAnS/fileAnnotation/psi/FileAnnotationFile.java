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
package se.ch.HAnS.fileAnnotation.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.fileAnnotation.FileAnnotationLanguage;

public class FileAnnotationFile extends PsiFileBase {

    public FileAnnotationFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FileAnnotationLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FileAnnotationFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "FileAnnotation file";
    }
}
