package se.ch.HAnS.featureModel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.ch.HAnS.AnnotationIcons;

import javax.swing.*;

public class FeatureModelFileType extends LanguageFileType {
    public static final FeatureModelFileType INSTANCE = new FeatureModelFileType();

    private FeatureModelFileType() {
        super(FeatureModelLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Feature Model File";
    }

    @Override
    public @NotNull String getDescription() {
        return "Feature model language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "feature-model";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AnnotationIcons.FileType;
    }
}
