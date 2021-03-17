package se.ch.HAnS.fileAnnotation;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.ch.HAnS.AnnotationIcons;

import javax.swing.*;

public class FileAnnotationFileType extends LanguageFileType {
    public static final FileAnnotationFileType INSTANCE = new FileAnnotationFileType();

    private FileAnnotationFileType() {
        super(FileAnnotationLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "File Annotation File";
    }

    @Override
    public @NotNull String getDescription() {
        return "File annotation language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "feature-to-file";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AnnotationIcons.FileType;
    }
}
