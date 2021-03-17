package se.ch.HAnS.folderAnnotation.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.folderAnnotation.FolderAnnotationLanguage;

public class FolderAnnotationTokenType extends IElementType {
    public FolderAnnotationTokenType(@NonNls @NotNull String debugName) {
        super(debugName, FolderAnnotationLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FolderAnnotationTokenType." + super.toString();
    }
}
