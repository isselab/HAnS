package se.ch.HAnS.codeAnnotations.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;

public class CodeAnnotationTokenType extends IElementType {
    public CodeAnnotationTokenType(@NotNull @NonNls String debugName) {
        super(debugName, Language.ANY);
    }

    @Override
    public String toString() {
        return "CodeAnnotation token type." + super.toString();
    }
}
