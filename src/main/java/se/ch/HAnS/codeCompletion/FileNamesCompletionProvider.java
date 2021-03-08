package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class FileNamesCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final boolean onlyManual;

    CompletionResultSet dictResult;

    public FileNamesCompletionProvider(boolean onlyManual) {
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
            if (!f.isDirectory()) {
                dictResult.addElement(LookupElementBuilder.create(f.getName()));
            }
        }
    }
}
