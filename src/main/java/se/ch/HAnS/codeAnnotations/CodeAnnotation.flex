package se.ch.HAnS.codeAnnotations;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.codeAnnotations.psi.CodeAnnotationTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;

%%

%class CodeAnnotationLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=[\n|\r\n]

CS = [,]

COMMENTMARKER = [.]

SPACE= [' ']

SEPARATOR=[:]

BEGIN = "&begin"
END = "&end"
LINE = "&line"

OBRACKET = ['('|'\['|'{']
CBRACKET = [')'|'\]'|'}']

FEATURENAME = [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+)]

%state WAITING_VALUE

%%
<YYINITIAL> {BEGIN}                                        { yybegin(YYINITIAL); return CodeAnnotationTypes.BEGIN; }
<YYINITIAL> {END}                                          { yybegin(YYINITIAL); return CodeAnnotationTypes.END; }
<YYINITIAL> {LINE}                                         { yybegin(YYINITIAL); return CodeAnnotationTypes.LINE; }

<YYINITIAL> {FEATURENAME}+                                 { yybegin(YYINITIAL); return CodeAnnotationTypes.FEATURENAME; }

<YYINITIAL> {COMMENTMARKER}+                               { yybegin(YYINITIAL); return CodeAnnotationTypes.COMMENTMARKER; }

<YYINITIAL> {SEPARATOR}{SEPARATOR}                         { yybegin(YYINITIAL); return CodeAnnotationTypes.SEPARATOR; }

<YYINITIAL> {CS}                                           { yybegin(YYINITIAL); return CodeAnnotationTypes.CS; }

<YYINITIAL> {OBRACKET}                                     { yybegin(YYINITIAL); return CodeAnnotationTypes.OBRACKET; }

<YYINITIAL> {CBRACKET}                                     { yybegin(YYINITIAL); return CodeAnnotationTypes.CBRACKET; }

<YYINITIAL> {SPACE}                                        { yybegin(YYINITIAL); return CodeAnnotationTypes.SPACE; }

<WAITING_VALUE> {CRLF}({CRLF}|{SPACE})+                    { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {SPACE}+                                   { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

({CRLF}|{SPACE})+                                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
