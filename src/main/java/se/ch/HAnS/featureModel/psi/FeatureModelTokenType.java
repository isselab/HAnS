package se.ch.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import se.ch.HAnS.featureModel.FeatureModelLanguage;
import org.jetbrains.annotations.NonNls;

public class FeatureModelTokenType extends IElementType {
    public FeatureModelTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FeatureModelLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FeatureModelTokenType." + super.toString();
    }
}
