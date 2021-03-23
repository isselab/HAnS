package se.ch.HAnS.featureModel.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;

import java.util.Collection;
import java.util.Objects;

public class FeatureModelPsiImplUtil {

    public static void addFeature(@NotNull FeatureModelFeature parent, @NotNull String featurename){
        Project project = ProjectManager.getInstance().getDefaultProject();
        PsiFile[] allFilenames = FilenameIndex.getFilesByName(project, ".feature-model", GlobalSearchScope.projectScope(project));
        PsiFile f;
        if (allFilenames.length > 0) {
            f = allFilenames[0];
        }else {
            Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(project, "feature-model");
            f = PsiManager.getInstance(project).findFile(c.iterator().next());
        }

        assert f != null;
        f.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                if (element == parent){
                    PsiComment c = new PsiCommentImpl(FeatureModelTypes.FEATURENAME, featurename);
                    parent.add(c);
                } else {
                    PsiComment c = new PsiCommentImpl(FeatureModelTypes.FEATURENAME, featurename);
                    if (findProjectName(project) != null) {
                        Objects.requireNonNull(findProjectName(project)).add(c);
                    }
                }
            }
        });
        if (parent == null){


        }
    }

    private static PsiElement findProjectName(Project project){
        PsiFile[] allFilenames = FilenameIndex.getFilesByName(project, ".feature-model", GlobalSearchScope.projectScope(project));
        PsiFile f;
        if (allFilenames.length > 0) {
            f = allFilenames[0];
        }
        else {
            Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(project, "feature-model");
            f = PsiManager.getInstance(project).findFile(c.iterator().next());
        }

        final PsiElement[] projectName = new FeatureModelProjectNameImpl[1];

        if (f!=null){
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof FeatureModelProjectNameImpl){
                        projectName[0] = element;
                    }
                }
            });
            return  projectName[0];
        }
        return null;
    }
}
