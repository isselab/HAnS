package se.ch.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import se.ch.HAnS.featureModel.FeatureModelLanguage;
import org.jetbrains.annotations.NonNls;

public class FeatureModelElementType extends IElementType {
    public FeatureModelElementType(@NotNull @NonNls String debugName) {
        super(debugName, FeatureModelLanguage.INSTANCE);
    }
}
