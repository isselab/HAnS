package featureModel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FeatureModelFileType extends LanguageFileType {
    public static final FeatureModelFileType INSTANCE = new FeatureModelFileType();

    private FeatureModelFileType() {
        super(FeatureModelLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Feature Model File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Feature model language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "feature-model";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return null;
        //return SimpleIcons.FILE;
    }
}
