package se.ch.HAnS.featureModel;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;

%%

%class FeatureModelLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=[\n|\r\n]
SPACE= [' ']

TAB=[\t]

FEATURENAME= [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]

%state WAITING_VALUE

%%

<YYINITIAL> {FEATURENAME}+                                 { yybegin(YYINITIAL); return FeatureModelTypes.FEATURENAME; }

<WAITING_VALUE> {CRLF}+                                    { yybegin(YYINITIAL); return FeatureModelTypes.CRLF; }

<WAITING_VALUE> ({SPACE}|{TAB})+                           { yybegin(YYINITIAL); return FeatureModelTypes.TAB; }

{CRLF}+                                                    { yybegin(YYINITIAL); return FeatureModelTypes.CRLF; }

({SPACE}|{TAB})+                                           { yybegin(YYINITIAL); return FeatureModelTypes.TAB; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
