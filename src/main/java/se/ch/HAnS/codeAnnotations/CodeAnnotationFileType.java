package se.ch.HAnS.codeAnnotations;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.ch.HAnS.AnnotationIcons;

import javax.swing.*;

public class CodeAnnotationFileType extends LanguageFileType {
    public static final CodeAnnotationFileType INSTANCE = new CodeAnnotationFileType();

    private CodeAnnotationFileType() {
        super(CodeAnnotationLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "Code Annotation File";
    }

    @Override
    public @NotNull String getDescription() {
        return "Code annotation language file";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "code-annotation";
    }

    @Override
    public @Nullable Icon getIcon() {
        return AnnotationIcons.FileType;
    }
}
