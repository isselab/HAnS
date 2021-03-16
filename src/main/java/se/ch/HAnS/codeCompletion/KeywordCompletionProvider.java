package se.ch.HAnS.codeCompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.ui.JBColor;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    public KeywordCompletionProvider() {
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters,
                                  @NotNull ProcessingContext context,
                                  @NotNull CompletionResultSet result) {

        result.addElement(LookupElementBuilder.create("&begin").withItemTextForeground(JBColor.GREEN));
        result.addElement(LookupElementBuilder.create("&end").withItemTextForeground(JBColor.GREEN));
        result.addElement(LookupElementBuilder.create("&line").withItemTextForeground(JBColor.GREEN));
    }
}
