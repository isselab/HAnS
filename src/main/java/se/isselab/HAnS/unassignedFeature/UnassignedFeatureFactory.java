/**
 Copyright 2023 Johan Martinson & Herman Jansson

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **/

package se.isselab.HAnS.unassignedFeature;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import se.isselab.HAnS.featureModel.FeatureModelFileType;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelFile;

public class UnassignedFeatureFactory {
    public static FeatureModelFeature createFeature(Project project, String name) {
        final FeatureModelFile file = createFile(project, name);
        return (FeatureModelFeature) file.getFirstChild();
    }

    public static FeatureModelFile createFile(Project project, String text) {
        String name = "dummy.feature-model";
        return (FeatureModelFile) PsiFileFactory.getInstance(project).createFileFromText(name, FeatureModelFileType.INSTANCE, text);
    }

    public static FeatureModelFeature createFeature(Project project, String name, String value) {
        final FeatureModelFile file = createFile(project, name + " = " + value);
        return (FeatureModelFeature) file.getFirstChild();
    }

    //adding a newline to the end of the file
    //CRLF=[\n]
    public static PsiElement createCRLF(Project project) {
        final FeatureModelFile file = createFile(project, "\n");
        return file.getFirstChild();
    }

    //INDENT=[\t]
    public static PsiElement createPlace(Project project) {
        final FeatureModelFile file = createFile(project, "\t");
        return file.getFirstChild();
    }
}
