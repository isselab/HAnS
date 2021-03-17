package se.ch.HAnS.fileAnnotation.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.ch.HAnS.fileAnnotation.FileAnnotationLanguage;

public class FileAnnotationElementType extends IElementType {
    public FileAnnotationElementType(@NotNull @NonNls String debugName) {
        super(debugName, FileAnnotationLanguage.INSTANCE);
    }
}
