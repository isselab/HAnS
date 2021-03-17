package se.ch.HAnS.fileAnnotation;

import com.intellij.lexer.FlexAdapter;

public class FileAnnotationLexerAdapter extends FlexAdapter {
    public FileAnnotationLexerAdapter() {
        super(new FileAnnotationLexer(null));
    }
}
