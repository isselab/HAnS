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

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.isselab.HAnS.fileAnnotation.FileAnnotationLanguage;

public class FileAnnotationTokenType extends IElementType {
    public FileAnnotationTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FileAnnotationLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FileAnnotationTokenType." + super.toString();
    }
}
