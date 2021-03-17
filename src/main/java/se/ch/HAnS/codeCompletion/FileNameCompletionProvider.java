package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.folderAnnotation.FolderAnnotationFileType;

import java.util.Objects;


/**
 * Provides file names for code completion.
 * Only adds files in current folder and excludes feature annotation files.
 */
public class FileNameCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final boolean onlyManual;

    CompletionResultSet dictResult;

    public FileNameCompletionProvider(boolean onlyManual) {
        this.onlyManual = onlyManual;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {
        if (parameters.isAutoPopup() && onlyManual) {
            return;
        }

        String prefix = result.getPrefixMatcher().getPrefix();
        if (prefix.isEmpty()) {
            return;
        }

        dictResult = result;

        PsiDirectory d = parameters.getOriginalFile().getParent();
        VirtualFile[] l = Objects.requireNonNull(d).getVirtualFile().getChildren();
        for (VirtualFile f : l) {
            if (!f.isDirectory() && f.getFileType() != FileAnnotationFileType.INSTANCE && f.getFileType() != FolderAnnotationFileType.INSTANCE) {
                dictResult.addElement(LookupElementBuilder.create(f.getName()).withItemTextForeground(JBColor.GREEN));
            }
        }
    }
}
