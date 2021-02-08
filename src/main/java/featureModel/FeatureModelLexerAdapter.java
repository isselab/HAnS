package featureModel;

import com.intellij.lexer.FlexAdapter;

public class FeatureModelLexerAdapter extends FlexAdapter {
    public FeatureModelLexerAdapter() {
        super(new FeatureModelLexer(null));
    }
}
