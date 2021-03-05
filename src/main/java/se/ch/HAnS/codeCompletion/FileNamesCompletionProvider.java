package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

        int lastSpace = prefix.lastIndexOf(' ');
        if (lastSpace >= 0 && lastSpace < prefix.length() - 1) {
            prefix = prefix.substring((lastSpace + 1));
            dictResult = result.withPrefixMatcher(prefix);
        } else {
            dictResult = result;
        }

        Project p = ProjectManager.getInstance().getOpenProjects()[0];
        VirtualFile @NotNull [] l = ProjectRootManager.getInstance(p).getContentSourceRoots();

        addFileNames(l);
    }

    private void addFileNames(VirtualFile[] v) {
        for (VirtualFile f : v) {
            if (f.isDirectory()) {
                addFileNames(f.getChildren());
            }
            else {
                dictResult.addElement(LookupElementBuilder.create(f.getName()));
            }
        }
    }
}
