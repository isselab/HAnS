package se.ch.HAnS.syntaxHighlighting.folderAnnotations;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.folderAnnotations.FolderAnnotationLexerAdapter;
import se.ch.HAnS.folderAnnotations.psi.FolderAnnotationTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class FolderAnnotationsSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey FEATURENAME =
            createTextAttributesKey("FEATURENAME", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey FEATURE_FOLDER_SEPARATOR =
            createTextAttributesKey("SEPARATOR", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey FEATURE_FOLDER_CS =
            createTextAttributesKey("CS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("HAnS_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] FEATURE_FOLDER_FEATURE_KEYS = new TextAttributesKey[]{FEATURENAME};
    private static final TextAttributesKey[] FEATURE_FOLDER_SEPARATOR_KEYS = new TextAttributesKey[]{FEATURE_FOLDER_SEPARATOR};
    private static final TextAttributesKey[] FEATURE_FOLDER_CS_KEYS = new TextAttributesKey[]{FEATURE_FOLDER_CS};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FolderAnnotationLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(FolderAnnotationTypes.FEATURENAME)){
            return FEATURE_FOLDER_FEATURE_KEYS;
        } else if (tokenType.equals(FolderAnnotationTypes.SEPARATOR)) {
            return FEATURE_FOLDER_SEPARATOR_KEYS;
        }else if (tokenType.equals(FolderAnnotationTypes.CS)) {
            return FEATURE_FOLDER_CS_KEYS;
        }else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
