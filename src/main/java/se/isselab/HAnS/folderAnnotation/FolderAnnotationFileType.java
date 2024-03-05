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
package se.isselab.HAnS.folderAnnotation;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.AnnotationIcons;

import javax.swing.*;

public class FolderAnnotationFileType extends LanguageFileType {
    public static final FolderAnnotationFileType INSTANCE = new FolderAnnotationFileType();

    public FolderAnnotationFileType() {
        super(FolderAnnotationLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Feature To Folder File";
    }

    @Override
    public @NotNull String getDescription() {
        return "Feature to folder language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "feature-to-folder";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AnnotationIcons.FileType;
    }
}
