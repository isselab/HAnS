package se.ch.HAnS;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.UseScopeEnlarger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FeatureAnnotationScopeEnlarger extends UseScopeEnlarger {
    @Nullable
    @Override
    public SearchScope getAdditionalUseScope(@NotNull PsiElement element) {
        // not restricting to GlobalSearchScope breaks refactorings like extract-variable
        if (element.getUseScope() instanceof GlobalSearchScope) {
            // any element can be referenced from an AsciiDoc element within the same project (directory, file, image, or an ID in an AsciiDoc file)
            return new FeatureAnnotationSearchScope(element.getProject()).restrictedByFileType();
        }
        return null;
    }
}