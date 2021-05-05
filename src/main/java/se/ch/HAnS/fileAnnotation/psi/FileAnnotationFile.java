package se.ch.HAnS.fileAnnotation.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.fileAnnotation.FileAnnotationLanguage;

public class FileAnnotationFile extends PsiFileBase {

    public FileAnnotationFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FileAnnotationLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FileAnnotationFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "FileAnnotation file";
    }
}
