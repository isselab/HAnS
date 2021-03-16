package se.ch.HAnS.codeAnnotations.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import se.ch.HAnS.codeAnnotations.CodeAnnotationFileType;
import se.ch.HAnS.codeAnnotations.CodeAnnotationLanguage;
import org.jetbrains.annotations.NotNull;

public class CodeAnnotationFile extends PsiFileBase {

    public CodeAnnotationFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CodeAnnotationLanguage.INSTANCE);
    }

    @Override
    public @NotNull FileType getFileType() {
        return CodeAnnotationFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "CodeAnnotation file";
    }
}
