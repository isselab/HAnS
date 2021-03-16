package se.ch.HAnS.codeAnnotations.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.ch.HAnS.codeAnnotations.CodeAnnotationLanguage;

public class CodeAnnotationTokenType extends IElementType {
    public CodeAnnotationTokenType(@NotNull @NonNls String debugName) {
        super(debugName, CodeAnnotationLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "CodeAnnotation token type." + super.toString();
    }
}
