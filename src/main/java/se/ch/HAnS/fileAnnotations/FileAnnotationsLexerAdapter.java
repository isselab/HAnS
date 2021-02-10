package se.ch.HAnS.fileAnnotations;

import com.intellij.lexer.FlexAdapter;
import se.ch.HAnS.fileAnnotations.FileAnnotationsLexer;

public class FileAnnotationsLexerAdapter extends FlexAdapter {
    public FileAnnotationsLexerAdapter() {
        super(new FileAnnotationsLexer(null));
    }
}
