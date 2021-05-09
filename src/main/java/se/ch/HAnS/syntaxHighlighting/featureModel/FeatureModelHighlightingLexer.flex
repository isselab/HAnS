package se.ch.HAnS.featureModel;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;
import java.util.ArrayDeque;
import java.util.Deque;

%%

%public
%class FeatureModelHighlightingLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=[\n|\r\n]
SPACE= [' ']

INDENT=[\t]

FEATURENAME= [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]

%state WAITING_VALUE

%%

<YYINITIAL> {FEATURENAME}+                                 { yybegin(YYINITIAL); return FeatureModelTypes.FEATURENAME; }

<WAITING_VALUE> {CRLF}+                                    { yybegin(YYINITIAL); return FeatureModelTypes.CRLF; }

<WAITING_VALUE> ({SPACE}|{INDENT})+                           { yybegin(YYINITIAL); return FeatureModelTypes.INDENT; }

{CRLF}+                                                    { yybegin(YYINITIAL); return FeatureModelTypes.CRLF; }

({SPACE}|{INDENT})+                                           { yybegin(YYINITIAL); return FeatureModelTypes.INDENT; }

[^]    { return TokenType.BAD_CHARACTER; }
