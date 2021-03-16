package se.ch.HAnS.testing;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class TestByAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project p = ProjectManager.getInstance().getOpenProjects()[0];

        PsiFile[] allFilenames = FilenameIndex.getFilesByName(p, ".feature-model", GlobalSearchScope.projectScope(p));
        for (PsiFile f:allFilenames) {
            System.out.println(f.getName());
        }
    }
}
