package se.ch.HAnS.fileAnnotations;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.ch.HAnS.AnnotationIcons;

import javax.swing.*;

public class FileAnnotationsFileType extends LanguageFileType {
    public static final FileAnnotationsFileType INSTANCE = new FileAnnotationsFileType();

    private FileAnnotationsFileType() {
        super(FileAnnotationsLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "File Annotations File";
    }

    @Override
    public @NotNull String getDescription() {
        return "File annotations language file";
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
