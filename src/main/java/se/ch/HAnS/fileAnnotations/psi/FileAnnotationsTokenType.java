package se.ch.HAnS.fileAnnotations.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.ch.HAnS.fileAnnotations.FileAnnotationsLanguage;

public class FileAnnotationsTokenType extends IElementType {
    public FileAnnotationsTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FileAnnotationsLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FileAnnotationsTokenType." + super.toString();
    }
}
