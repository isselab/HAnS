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
package se.isselab.HAnS.featureModel;

import com.intellij.psi.tree.IElementType;
import se.isselab.HAnS.featureModel.psi.FeatureModelTypes;
import com.intellij.psi.TokenType;

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
