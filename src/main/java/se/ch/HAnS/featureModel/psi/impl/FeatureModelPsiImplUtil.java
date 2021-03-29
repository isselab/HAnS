package se.ch.HAnS.featureModel.psi.impl;

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FeatureModelPsiImplUtil {

    private static Logger LOGGER = Logger.getLogger("utilClass");

    public static void addFeature(@NotNull FeatureModelFeature parent, @NotNull String featurename){
        Project project = parent.getProject();
        PsiFile f = parent.getContainingFile();
        VirtualFile vf = f.getVirtualFile();

        LOGGER.log(Level.INFO, vf.toString());
        parent.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                PsiComment c = new PsiCommentImpl(FeatureModelTypes.FEATURENAME, featurename);
                if (element == parent && element instanceof FeatureModelFeatureImpl){
                    LOGGER.log(Level.INFO, "element in util: " + element.getText());
                    //((FeatureModelFeatureImpl) element).getFeature().add(c);

                    element.add(c);
                    //element.getNode().addChild(c.getNode());
                } else {
                    LOGGER.log(Level.INFO, "element:\n" + element.getText() + "\n parent: " + parent.getText());
                    if (findProjectName(f) != null) {
                        Objects.requireNonNull(findProjectName(f)).add(c);
                    }
                }
                super.visitElement(element);
            }
        });

        /*f.accept(new PsiRecursiveElementWalkingVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                PsiComment c = new PsiCommentImpl(FeatureModelTypes.FEATURENAME, featurename);
                if (element == parent && element instanceof FeatureModelFeatureImpl){
                    LOGGER.log(Level.INFO, "element in util: " + element.getText());
                    ((FeatureModelFeatureImpl) element).getFeature().add(c);
                    element.getNode().addChild(c.getNode());
                    //element.add(c);
                } else {
                    LOGGER.log(Level.INFO, "element:\n" + element.getText() + "\n parent: " + parent.getText());
                    if (findProjectName(f) != null) {
                        Objects.requireNonNull(findProjectName(f)).add(c);
                    }
                }
                super.visitElement(element);
            }
        });*/
    }

    private static PsiElement findProjectName(PsiFile f){

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
