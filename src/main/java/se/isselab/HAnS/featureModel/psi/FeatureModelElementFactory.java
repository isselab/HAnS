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
package se.isselab.HAnS.featureModel.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import se.isselab.HAnS.featureModel.FeatureModelFileType;

public class FeatureModelElementFactory {

    public static FeatureModelFeature createFeature(Project project, String name) {
        final FeatureModelFile file = createFile(project, name);
        return (FeatureModelFeature) file.getFirstChild();
    }

    public static FeatureModelFile createFile(Project project, String text) {
        String name = "dummy.feature-model";
        return (FeatureModelFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FeatureModelFileType.INSTANCE, text);
    }
}