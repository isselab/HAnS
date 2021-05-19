package se.ch.HAnS.referencing;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameInputValidator;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;

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
