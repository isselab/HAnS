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
package se.isselab.HAnS.featureModel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.AnnotationIcons;

import javax.swing.*;

public class FeatureModelFileType extends LanguageFileType {
    public static final FeatureModelFileType INSTANCE = new FeatureModelFileType();

    private FeatureModelFileType() {
        super(FeatureModelLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Feature Model File";
    }

    @Override
    public @NotNull String getDescription() {
        return "Feature model language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "feature-model";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AnnotationIcons.FeatureModelIcon;
    }
}
