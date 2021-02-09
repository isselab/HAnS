package se.ch.HAnS.featureModel.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import se.ch.HAnS.featureModel.FeatureModelFileType;
import se.ch.HAnS.featureModel.FeatureModelLanguage;
import org.jetbrains.annotations.NotNull;

public class FeatureModelFile extends PsiFileBase {

    public FeatureModelFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FeatureModelLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FeatureModelFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "FeatureModel file";
    }
}
