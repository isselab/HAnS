package se.ch.HAnS.fileAnnotations;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.fileAnnotations.psi.FileAnnotationsTypes;
import com.intellij.lexer.FlexLexer;
import com.intellij.psi.TokenType;

%%

%class FileAnnotationsLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=[\n|\r\n]

NEWLINE=[\n]

CS = [\,]

CM = [\"]

SPACE= [' ']

SEPARATOR=[:]

DOT=[\.]

STRING = [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+)]

%state WAITING_VALUE

%%

<YYINITIAL> {STRING}+                                      { yybegin(YYINITIAL); return FileAnnotationsTypes.STRING; }

<YYINITIAL> {SEPARATOR}{SEPARATOR}                         { yybegin(YYINITIAL); return FileAnnotationsTypes.SEPARATOR; }

<YYINITIAL> {SEPARATOR}                                    { yybegin(YYINITIAL); return FileAnnotationsTypes.COLON; }

<YYINITIAL> {DOT}                                          { yybegin(YYINITIAL); return FileAnnotationsTypes.DOT; }

<YYINITIAL> {CS}                                           { yybegin(YYINITIAL); return FolderAnnotationTypes.CS; }

<YYINITIAL> {CM}                                           { yybegin(YYINITIAL); return FolderAnnotationTypes.CM; }

<YYINITIAL> {NEWLINE}                                      { yybegin(YYINITIAL); return FileAnnotationsTypes.NEWLINE; }

<WAITING_VALUE> {CRLF}({CRLF}|{SPACE})+                    { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {SPACE}+                                   { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

({CRLF}|{SPACE})+                                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
