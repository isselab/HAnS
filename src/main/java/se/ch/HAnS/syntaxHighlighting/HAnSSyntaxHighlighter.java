package se.ch.HAnS.syntaxHighlighting;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;


import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.FeatureModelLexerAdapter;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class HAnSSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey FEATURE =
            createTextAttributesKey("FEATURE", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("HAnS_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] FEATURE_KEYS = new TextAttributesKey[]{FEATURE};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FeatureModelLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(FeatureModelTypes.FEATURENAME)){
            return FEATURE_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
