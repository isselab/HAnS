package se.ch.HAnS.codeAnnotations.psi;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CodeAnnotationElementType extends IElementType {
    public CodeAnnotationElementType(@NotNull @NonNls String debugName) {
        super(debugName, Language.ANY);
    }
}
