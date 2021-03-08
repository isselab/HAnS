package se.ch.HAnS.testing;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.PsiCommentImpl;
import com.intellij.psi.impl.source.tree.PsiWhiteSpaceImpl;
import com.intellij.psi.impl.source.xml.XmlCommentImpl;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.codeAnnotations.psi.CodeAnnotationTypes;
import se.ch.HAnS.codeAnnotations.psi.impl.CodeAnnotationBeginmarkerImpl;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

public class TestByAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
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
    }
}
