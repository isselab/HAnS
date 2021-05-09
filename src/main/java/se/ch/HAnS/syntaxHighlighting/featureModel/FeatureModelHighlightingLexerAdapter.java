package se.ch.HAnS.syntaxHighlighting.featureModel;

import com.intellij.lexer.FlexAdapter;
import se.ch.HAnS.featureModel.FeatureModelHighlightingLexer;

public class FeatureModelHighlightingLexerAdapter extends FlexAdapter {
    public FeatureModelHighlightingLexerAdapter() {
        super(new FeatureModelHighlightingLexer(null));
    }
}