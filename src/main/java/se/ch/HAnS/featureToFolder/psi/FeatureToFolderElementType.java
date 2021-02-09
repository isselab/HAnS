package se.ch.HAnS.featureToFolder.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureToFolder.FeatureToFolderLanguage;

public class FeatureToFolderElementType extends IElementType {
    public FeatureToFolderElementType(@NonNls @NotNull String debugName) {
        super(debugName, FeatureToFolderLanguage.INSTANCE);
    }
}
