package se.ch.HAnS.fileAnnotations;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;
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

SPACE= [' ']

SEPARATOR=[:]

BEGIN = [&begin]
END = [&end]
LINE = [&line]

OBRACKET = ['('|'\['|'{']
CBRACKET = [')'|'\]'|'}']

FEATURENAME = [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+)]

%state WAITING_VALUE

%%

<YYINITIAL> {FEATURENAME}+                                 { yybegin(YYINITIAL); return CodeAnnotationTypes.STRING; }

<YYINITIAL> {SEPARATOR}{SEPARATOR}                         { yybegin(YYINITIAL); return CodeAnnotationTypes.SEPARATOR; }

<YYINITIAL> {CS}                                           { yybegin(YYINITIAL); return CodeAnnotationTypes.CS; }

<YYINITIAL> {OBRACKET}|{SPACE}                             { yybegin(YYINITIAL); return CodeAnnotionTypes.OBRACKET; }

<YYINITIAL> {CBRACKET}|{SPACE}                             { yybegin(YYINITIAL); return CodeAnnotationTypes.CBRACKET; }

<WAITING_VALUE> {CRLF}({CRLF}|{SPACE})+                    { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {SPACE}+                                   { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

({CRLF}|{SPACE})+                                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
