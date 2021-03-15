package se.ch.HAnS.codeAnnotations.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.ch.HAnS.codeAnnotations.CodeAnnotationsLanguage;

public class CodeAnnotationTokenType extends IElementType {
    public CodeAnnotationTokenType(@NotNull @NonNls String debugName) {
        super(debugName, CodeAnnotationsLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "CodeAnnotation token type." + super.toString();
    }
}
