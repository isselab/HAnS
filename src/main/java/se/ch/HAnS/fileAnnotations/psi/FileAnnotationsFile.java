package se.ch.HAnS.fileAnnotations.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.fileAnnotations.FileAnnotationsFileType;
import se.ch.HAnS.fileAnnotations.FileAnnotationsLanguage;

public class FileAnnotationsFile extends PsiFileBase {

    public FileAnnotationsFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, FileAnnotationsLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return FileAnnotationsFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "FileAnnotations file";
    }
}
