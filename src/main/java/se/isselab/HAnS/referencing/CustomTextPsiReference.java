package se.isselab.HAnS.referencing;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import org.jetbrains.annotations.NotNull;

public class CustomTextPsiReference extends PsiReferenceBase<PsiElement> {
    private final String customName;

    public CustomTextPsiReference(@NotNull PsiElement element, String customName) {
        super(element);
        this.customName = customName;
    }

    @Override
    public @NotNull String getCanonicalText() {
        return customName;
    }

    @Override
    public PsiElement resolve() {
        return getElement();
    }
}
