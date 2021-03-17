package se.ch.HAnS.folderAnnotation;

import com.intellij.lexer.FlexAdapter;

public class FolderAnnotationLexerAdapter extends FlexAdapter {
    public FolderAnnotationLexerAdapter() {
        super(new FolderAnnotationLexer(null));
    }
}
