/*
Copyright 2024 Herman Jansson & Johan Martinson

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
package se.isselab.HAnS.referencing.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class FeatureAnnotationNamedElementImpl extends ASTWrapperPsiElement {

    private int tanglingDegree;
    private int scatteringDegree;
    private int lineCount;
    private int maxNestingDepth;
    private int minNestingDepth;
    private double avgNestingDepth;
    private int numberOfAnnotatedFiles;
    private int numberOfFileAnnotations;
    private int numberOfFolderAnnotations;

    public FeatureAnnotationNamedElementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }


    public int getTanglingDegree() {
        return tanglingDegree;
    }

    public void setTanglingDegree(int tanglingDegree) {
        this.tanglingDegree = tanglingDegree;
    }

    public int getScatteringDegree() {
        return scatteringDegree;
    }

    public void setScatteringDegree(int scatteringDegree) {
        this.scatteringDegree = scatteringDegree;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public int getMaxNestingDepth() {
        return maxNestingDepth;
    }

    public void setMaxNestingDepth(int maxNestingDepth) {
        this.maxNestingDepth = maxNestingDepth;
    }

    public int getMinNestingDepth() {
        return minNestingDepth;
    }

    public void setMinNestingDepth(int minNestingDepth) {
        this.minNestingDepth = minNestingDepth;
    }

    public double getAvgNestingDepth() {
        return avgNestingDepth;
    }

    public void setAvgNestingDepth(double avgNestingDepth) {
        this.avgNestingDepth = avgNestingDepth;
    }

    public int getNumberOfAnnotatedFiles() {
        return numberOfAnnotatedFiles;
    }
    public void setNumberOfAnnotatedFiles(int numberOfAnnotatedFiles) {
        this.numberOfAnnotatedFiles = numberOfAnnotatedFiles;
    }

    public int getNumberOfFolderAnnotations() {
        return numberOfFolderAnnotations;
    }

    public void setNumberOfFolderAnnotations(int numberOfFolderAnnotations) {
        this.numberOfFolderAnnotations = numberOfFolderAnnotations;
    }

    public int getNumberOfFileAnnotations() {
        return numberOfFileAnnotations;
    }

    public void setNumberOfFileAnnotations(int numberOfFileAnnotations) {
        this.numberOfFileAnnotations = numberOfFileAnnotations;
    }

}