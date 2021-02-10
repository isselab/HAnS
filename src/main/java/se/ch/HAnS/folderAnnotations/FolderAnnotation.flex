package se.ch.HAnS.folderAnnotations;

import com.intellij.psi.tree.IElementType;
import se.ch.HAnS.folderAnnotations.psi.FolderAnnotationTypes;
import com.intellij.psi.TokenType;
import com.intellij.lexer.FlexLexer;

%%

%class FolderAnnotationLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=[\n|\r\n]
SPACE= [' ']
SEPARATOR = [:]
CS = [,]

FEATURENAME= [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]

%state WAITING_VALUE

%%

<YYINITIAL> {FEATURENAME}+                                 { yybegin(YYINITIAL); return FolderAnnotationTypes.FEATURENAME; }
<YYINITIAL> {SEPARATOR}{SEPARATOR}                         { yybegin(YYINITIAL); return FolderAnnotationTypes.SEPARATOR; }
<YYINITIAL> {CS}                                           { yybegin(YYINITIAL); return FolderAnnotationTypes.CS; }

<WAITING_VALUE> {CRLF}({CRLF}|{SPACE})+                    { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {SPACE}+                                   { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

({CRLF}|{SPACE})+                                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
