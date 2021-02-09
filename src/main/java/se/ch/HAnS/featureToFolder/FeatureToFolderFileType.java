package se.ch.HAnS.featureToFolder;

import javax.swing.Icon;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FeatureToFolderFileType extends LanguageFileType {
    public static final FeatureToFolderFileType INSTANCE = new FeatureToFolderFileType();

    public FeatureToFolderFileType() {
        super(FeatureToFolderLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Feature To Folder File";
    }

    @Override
    public @NotNull String getDescription() {
        return "Feature to folder language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "feature-to-folder";
    }

    @Override
    public @Nullable Icon getIcon() {
        return null;
    }
}
