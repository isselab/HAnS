package se.ch.HAnS.featureToFolder;

import com.intellij.lexer.FlexAdapter;

public class FeatureToFolderLexerAdapter extends FlexAdapter {
    public FeatureToFolderLexerAdapter() {
        super(new FeatureToFolderLexer(null));
    }
}
