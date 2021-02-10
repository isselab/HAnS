package se.ch.HAnS.folderAnnotations;

import com.intellij.lexer.FlexAdapter;

public class FolderAnnotationLexerAdapter extends FlexAdapter {
    public FolderAnnotationLexerAdapter() {
        super(new FolderAnnotationLexer(null));
    }
}
