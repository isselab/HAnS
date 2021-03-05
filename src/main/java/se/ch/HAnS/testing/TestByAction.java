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

    PsiElement tmp;
    PsiElement second_tmp;
    boolean search = true;
    boolean b = false;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // testcomment
        PsiFile f = e.getData(LangDataKeys.PSI_FILE);
        if (f != null) {
            f.accept(new PsiRecursiveElementWalkingVisitor() {
                @Override
                public void visitElement(@NotNull PsiElement element) {
                    if (element instanceof PsiWhiteSpace && search) {
                        second_tmp = element;
                        search = false;
                    }
                    if (element.toString().equals("FeatureModelFeatureImpl(FEATURE)")) {//element instanceof PsiComment) {
                        Project p = ProjectManager.getInstance().getOpenProjects()[0];

                        if (element.getText().equals("test")) {
                            WriteCommandAction.runWriteCommandAction(p, () -> {
                                tmp = element.copy();
                                element.add(second_tmp);
                                b = true;
                            });
                        }
                    }
                    if (b) {
                        element.add(tmp);
                        b = false;
                    }
                    super.visitElement(element);
                }
            });
        }
    }
}
