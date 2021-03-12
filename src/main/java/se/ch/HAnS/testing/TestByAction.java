package se.ch.HAnS.testing;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

public class TestByAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Project p = ProjectManager.getInstance().getOpenProjects()[0];
        @NotNull PsiFile @NotNull [] l = FilenameIndex.getFilesByName(p, "_.feature-model", GlobalSearchScope.projectScope(p));
        for (PsiFile f:l) {
            System.out.println(f);
        }

        /*
        // test comment
        PsiFile f = e.getData(LangDataKeys.PSI_FILE);
        if (f != null) {
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof FeatureModelFeatureImpl) {
                        Project p = ProjectManager.getInstance().getOpenProjects()[0];

                        if (element.getText().equals("test")) {
                            WriteCommandAction.runWriteCommandAction(p, () -> {
                            });
                        }
                    }
                    super.visitElement(element);
                }
            });
        }
        */
    }
}
