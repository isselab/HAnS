package se.ch.HAnS.fileAnnotations.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import org.jetbrains.annotations.NonNls;
import se.ch.HAnS.fileAnnotations.FileAnnotationsLanguage;

public class FileAnnotationsElementType extends IElementType {
    public FileAnnotationsElementType(@NotNull @NonNls String debugName) {
        super(debugName, FileAnnotationsLanguage.INSTANCE);
    }
}
