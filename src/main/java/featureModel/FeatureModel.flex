package featureModel;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import featureModel.psi.FeatureModelTypes;
import com.intellij.psi.TokenType;

%%

%class FeatureModelLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

SPACE= ' '* -> skip ;

KEYWORDS= ('or'|'xor'|'?') -> skip ;

FEATURENAME= ([A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+)+;

%state WAITING_VALUE

%%

<YYINITIAL> {FEATURENAME}                                { yybegin(YYINITIAL); return FeatureModelTypes.FEATURENAME; }

<WAITING_VALUE> {SPACE}                                  { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

[^]                                                      { return TokenType.BAD_CHARACTER; }
