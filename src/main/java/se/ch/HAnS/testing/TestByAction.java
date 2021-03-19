package se.ch.HAnS.testing;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelProjectNameImpl;

import java.util.Collection;

public class TestByAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project p = ProjectManager.getInstance().getOpenProjects()[0];

        PsiFile[] allFilenames = FilenameIndex.getFilesByName(p, ".feature-model", GlobalSearchScope.projectScope(p));
        PsiFile f;
        if (allFilenames.length > 0) {
            f = allFilenames[0];
        }
        else {
            Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(p, "feature-model");
            f = PsiManager.getInstance(p).findFile(c.iterator().next());
        }

        if (f != null) {
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof FeatureModelProjectNameImpl){
                        System.out.println("Project name: " + element.getText());
                    }
                    else if (element instanceof FeatureModelFeatureImpl) {
                        System.out.println("Feature name: " + element.getText());
                    }
                    super.visitElement(element);
                }
            });
        }
    }
}
