package se.ch.HAnS.featureToFolder.psi;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureToFolder.FeatureToFolderLanguage;

public class FeatureToFolderTokenType extends IElementType {
    public FeatureToFolderTokenType(@NonNls @NotNull String debugName) {
        super(debugName, FeatureToFolderLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FeatureToFolderTokenType." + super.toString();
    }
}
