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
package se.isselab.HAnS.codeAnnotation;

import com.intellij.psi.tree.IElementType;
import se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationTypes;
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

COMMENTMARKER = [\S]

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

<YYINITIAL> {SEPARATOR}{SEPARATOR}                         { yybegin(YYINITIAL); return CodeAnnotationTypes.SEPARATOR; }

<YYINITIAL> {CS}                                           { yybegin(YYINITIAL); return CodeAnnotationTypes.CS; }

<YYINITIAL> {OBRACKET}                                     { yybegin(YYINITIAL); return CodeAnnotationTypes.OBRACKET; }

<YYINITIAL> {CBRACKET}                                     { yybegin(YYINITIAL); return CodeAnnotationTypes.CBRACKET; }

<YYINITIAL> {SPACE}                                        { yybegin(YYINITIAL); return CodeAnnotationTypes.SPACE; }

<WAITING_VALUE> {CRLF}({CRLF}|{SPACE})+                    { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<WAITING_VALUE> {SPACE}+                                   { yybegin(WAITING_VALUE); return TokenType.WHITE_SPACE; }

<YYINITIAL> {COMMENTMARKER}                                { yybegin(YYINITIAL); return CodeAnnotationTypes.COMMENTMARKER; }

({CRLF}|{SPACE})+                                          { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

[^]                                                        { return TokenType.BAD_CHARACTER; }
