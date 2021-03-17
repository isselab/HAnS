package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.psi.PsiComment;
import org.jetbrains.annotations.NotNull;

public class CommentContext extends TemplateContextType {

    protected CommentContext() {
        super("COMMENT", "Comment");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        return templateActionContext.getFile().findElementAt(templateActionContext.getEditor().getCaretModel().getOffset()) instanceof PsiComment;
    }

}