package se.ch.HAnS.codeAnnotations;

import com.intellij.lexer.FlexAdapter;

public class CodeAnnotationLexerAdapter extends FlexAdapter {
    public CodeAnnotationLexerAdapter() {
        super(new CodeAnnotationLexer(null));
    }
}
