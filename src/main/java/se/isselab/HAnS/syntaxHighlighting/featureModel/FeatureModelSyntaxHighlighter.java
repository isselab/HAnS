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
package se.isselab.HAnS.syntaxHighlighting.featureModel;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;


import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class FeatureModelSyntaxHighlighter extends SyntaxHighlighterBase {

    public static final TextAttributesKey FEATURE =
            createTextAttributesKey("FEATURENAME", DefaultLanguageHighlighterColors.CONSTANT);
    public static final TextAttributesKey BAD_CHARACTER =
            createTextAttributesKey("HAnS_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);
    public static final TextAttributesKey XOR =
            createTextAttributesKey("XOR_TOKEN", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OR =
            createTextAttributesKey("OR_TOKEN", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OPTIONAL =
            createTextAttributesKey("OPTIONAL", DefaultLanguageHighlighterColors.OPERATION_SIGN);

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] FEATURE_KEYS = new TextAttributesKey[]{FEATURE};
    private static final TextAttributesKey[] XOR_KEYS = new TextAttributesKey[]{XOR};
    private static final TextAttributesKey[] OR_KEYS = new TextAttributesKey[]{OR};
    private static final TextAttributesKey[] OPTIONAL_KEYS = new TextAttributesKey[]{OPTIONAL};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @Override
    public @NotNull Lexer getHighlightingLexer() {
        return new FeatureModelHighlightingLexerAdapter();
    }

    @Override
    public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
        // System.out.println("Highlighting token: " + tokenType); // Temporary debug
        if (tokenType.equals(FeatureModelTypes.FEATURENAME)){
            return FEATURE_KEYS;
        }else if (tokenType.equals(FeatureModelTypes.XOR_TOKEN)) {
            return XOR_KEYS;
        } else if (tokenType.equals(FeatureModelTypes.OR_TOKEN)) {
            return OR_KEYS;
        } else if (tokenType.equals(FeatureModelTypes.OPTIONAL)) {
            return OPTIONAL_KEYS;
        } else if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
