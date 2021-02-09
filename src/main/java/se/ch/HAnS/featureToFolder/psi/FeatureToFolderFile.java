package se.ch.HAnS.featureToFolder.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureToFolder.FeatureToFolderFileType;
import se.ch.HAnS.featureToFolder.FeatureToFolderLanguage;

public class FeatureToFolderFile extends PsiFileBase {
    public FeatureToFolderFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FeatureToFolderLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FeatureToFolderFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "FeatureToFolder file";
    }
}
