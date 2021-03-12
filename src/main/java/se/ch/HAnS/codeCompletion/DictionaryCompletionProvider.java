package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

import java.util.Collection;

public class DictionaryCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final boolean onlyManual;

    public DictionaryCompletionProvider(boolean onlyManual) {
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
        CompletionResultSet dictResult;

        int lastSpace = prefix.lastIndexOf(' ');
        if (lastSpace >= 0 && lastSpace < prefix.length() - 1) {
            prefix = prefix.substring((lastSpace + 1));
            dictResult = result.withPrefixMatcher(prefix);
        } else {
            dictResult = result;
        }

        Project p = ProjectManager.getInstance().getOpenProjects()[0];

        // The following line does not work for files consisting of only extension
        Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(p, "feature-model");
        PsiFile f = PsiManager.getInstance(p).findFile(c.iterator().next());
        if (f != null) {
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof FeatureModelFeatureImpl){
                        dictResult.addElement(LookupElementBuilder.create(element.getText()));
                    }
                    super.visitElement(element);
                }
            });
        }
    }
}
