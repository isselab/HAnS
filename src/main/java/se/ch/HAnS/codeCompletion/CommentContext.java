package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.Language;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public class CommentContext extends TemplateContextType {

    public CommentContext() {
        super("COMMENT", "Comment");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        PsiFile file = templateActionContext.getFile();
        final int offset = templateActionContext.getStartOffset();
        if (PsiUtilCore.getLanguageAtOffset(file, offset).isKindOf(Language.ANY)){
            PsiElement element = file.findElementAt(offset);
            if (element instanceof PsiWhiteSpace && offset > 0) {
                element = file.findElementAt(offset-1);
            }
            return PsiTreeUtil.getParentOfType(element, PsiComment.class, false) != null;
        }
        return false;

    }

}