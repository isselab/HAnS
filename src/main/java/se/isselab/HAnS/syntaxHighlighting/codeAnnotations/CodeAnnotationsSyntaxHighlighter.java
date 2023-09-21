/*
Copyright 2021 Herman Jansson & Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.syntaxHighlighting.codeAnnotations;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.codeAnnotation.CodeAnnotationLexerAdapter;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class CodeAnnotationsSyntaxHighlighter extends SyntaxHighlighterBase {


    public static final TextAttributesKey KEYWORD =
            createTextAttributesKey("EA_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey SEPARATOR =
            createTextAttributesKey("SEPARATOR", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey CS =
            createTextAttributesKey("CS", DefaultLanguageHighlighterColors.BRACKETS);
    public static final TextAttributesKey COMMENTMARKER =
            createTextAttributesKey("COMMENTMARKER", DefaultLanguageHighlighterColors.LINE_COMMENT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("FA_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);


    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] CS_KEYS = new TextAttributesKey[]{CS};
    private static final TextAttributesKey[] COMMENTMARKER_KEYS = new TextAttributesKey[]{COMMENTMARKER};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new CodeAnnotationLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(CodeAnnotationTypes.BEGIN) ||
                tokenType.equals(CodeAnnotationTypes.END) ||
                tokenType.equals(CodeAnnotationTypes.LINE))  {
            return KEYWORD_KEYS;
        } else if (tokenType.equals(CodeAnnotationTypes.SEPARATOR)) {
            return SEPARATOR_KEYS;
        } else if (tokenType.equals(CodeAnnotationTypes.CS) ||
                tokenType.equals(CodeAnnotationTypes.OBRACKET) ||
                tokenType.equals(CodeAnnotationTypes.CBRACKET)) {
            return CS_KEYS;
        } else if (tokenType.equals(CodeAnnotationTypes.COMMENTMARKER)) {
            return COMMENTMARKER_KEYS;
        }
        else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
