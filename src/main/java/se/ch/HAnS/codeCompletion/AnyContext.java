package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import org.jetbrains.annotations.NotNull;

public class AnyContext extends TemplateContextType {
    protected AnyContext() {
        super("ANY", "Any");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
        return true;
    }


}
