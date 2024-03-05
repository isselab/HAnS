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

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.regex.Pattern;

public class FeatureNameInputValidator implements RenameInputValidator {
    @Override
    public @NotNull ElementPattern<? extends PsiElement> getPattern() {
        return PlatformPatterns.psiElement(FeatureModelFeature.class);
    }

    @Override
    public boolean isInputValid(@NotNull String newName, @NotNull PsiElement element, @NotNull ProcessingContext context) {
        return Pattern.matches("[[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]+", newName) && checkNonConflictingName(newName, element);
    }

    private boolean checkNonConflictingName(String newElementName, PsiElement element) {
        if (element != null) {
            PsiElement [] l = element.getParent().getChildren();
            for (PsiElement e:l) {
                if (e.getFirstChild().getText().equals(newElementName)) {
                    return false;
                }
            }
        }
        return true;
    }
}
