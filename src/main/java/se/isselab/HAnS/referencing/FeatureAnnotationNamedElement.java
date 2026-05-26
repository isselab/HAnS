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

import com.intellij.psi.PsiNameIdentifierOwner;

public interface FeatureAnnotationNamedElement extends PsiNameIdentifierOwner {

    int getTanglingDegree();
    void setTanglingDegree(int tanglingDegree);

    int getScatteringDegree();
    void setScatteringDegree(int scatteringDegree);

    int getLineCount();
    void setLineCount(int lineCount);

    int getMaxNestingDepth();
    void setMaxNestingDepth(int maxNestingDepth);

    int getMinNestingDepth();
    void setMinNestingDepth(int minNestingDepth);

    double getAvgNestingDepth();
    void setAvgNestingDepth(double avgNestingDepth);

    int getNumberOfAnnotatedFiles();
    void setNumberOfAnnotatedFiles(int numberOfAnnotatedFiles);

    int getNumberOfFolderAnnotations();
    void setNumberOfFolderAnnotations(int numberOfFolderAnnotations);

    int getNumberOfFileAnnotations();
    void setNumberOfFileAnnotations(int numberOfFileAnnotations);
}