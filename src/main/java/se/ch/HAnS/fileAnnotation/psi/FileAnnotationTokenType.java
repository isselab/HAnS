package se.ch.HAnS.fileAnnotation.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.ch.HAnS.fileAnnotation.FileAnnotationLanguage;

public class FileAnnotationTokenType extends IElementType {
    public FileAnnotationTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FileAnnotationLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FileAnnotationTokenType." + super.toString();
    }
}
