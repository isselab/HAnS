package se.ch.HAnS.fileAnnotation;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;

%%

%class FileAnnotationLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=[\n|\r\n]

NEWLINE=[\n]

CS = [,]

CM = [\"]

SPACE= [' ']

SEPARATOR=[:]

DOT=[\.]

STRING = [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+)]

%state WAITING_VALUE

%%

<YYINITIAL> {STRING}+                                      { yybegin(YYINITIAL); return FileAnnotationTypes.STRING; }

<YYINITIAL> {SEPARATOR}{SEPARATOR}                         { yybegin(YYINITIAL); return FileAnnotationTypes.SEPARATOR; }

<YYINITIAL> {SEPARATOR}                                    { yybegin(YYINITIAL); return FileAnnotationTypes.COLON; }

<YYINITIAL> {DOT}                                          { yybegin(YYINITIAL); return FileAnnotationTypes.DOT; }

<YYINITIAL> {CS}                                           { yybegin(YYINITIAL); return FileAnnotationTypes.CS; }

<YYINITIAL> {CM}                                           { yybegin(YYINITIAL); return FileAnnotationTypes.CM; }

<YYINITIAL> {NEWLINE}+                                     { yybegin(YYINITIAL); return FileAnnotationTypes.NEWLINE; }

<WAITING_VALUE> {CRLF}({CRLF}|{SPACE})+                    { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {SPACE}+                                   { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

({CRLF}|{SPACE})+                                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
