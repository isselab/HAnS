package se.ch.HAnS.folderAnnotations;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class FolderAnnotationFileType extends LanguageFileType {
    public static final FolderAnnotationFileType INSTANCE = new FolderAnnotationFileType();

    public FolderAnnotationFileType() {
        super(FolderAnnotationLanguage.INSTANCE);
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
