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
import java.util.ArrayDeque;
import java.util.Deque;

%%

%public
%class FeatureModelLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{
    return;
%eof}

CRLF=[\n|\r\n]
SPACE= [' ']

INDENT=[\t]

FEATURENAME= [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]

%{
    int current_line_indent = 0;
    int indent_level = 0;
    int indent_caller = indent;

    final int TAB_WIDTH = 4;

    int test = 1;

    Deque<Integer> indent_levels = new ArrayDeque<Integer>();
%}

%x indent
%s feature
%s dedent

%%
<YYINITIAL>.         { yypushback(1); indent_levels.push(0); yybegin(feature); }

<indent>{SPACE}      { current_line_indent++; }
<indent>{INDENT}     { current_line_indent = (current_line_indent + TAB_WIDTH) & ~(TAB_WIDTH-1); }
<indent>{CRLF}+      { current_line_indent = 0; return FeatureModelTypes.CRLF; }

<dedent>. {
    indent_levels.pop();
    if(current_line_indent != indent_levels.peek()) {
        yypushback(1);
        return FeatureModelTypes.DEDENT;
    }
    else {
        yypushback(1);
        yybegin(feature);
        return FeatureModelTypes.DEDENT;
    }
}

<indent>{FEATURENAME}       {
        if(current_line_indent > indent_levels.peek()) {
            indent_levels.push(current_line_indent);
            yypushback(1);
            yybegin(feature);
            return FeatureModelTypes.INDENT;
        }
        else if(current_line_indent < indent_levels.peek()) {
            indent_levels.pop();
            if (current_line_indent > indent_levels.peek()) {
                indent_levels.push(current_line_indent);
                yypushback(1);
                yybegin(feature);
            }
            else if(current_line_indent != indent_levels.peek()) {
                yypushback(1);
                yybegin(dedent);
                return FeatureModelTypes.DEDENT;
            }
            else {
                yypushback(1);
                yybegin(feature);
                return FeatureModelTypes.DEDENT;
            }
        }
        else if (current_line_indent == 0){
            yypushback(1);
            yybegin(feature);
        }
        else {
            yypushback(1);
            yybegin(feature);
            return FeatureModelTypes.CRLF;
        }
}

<indent><<EOF>> {
    if (indent_levels.peek() != 0) {
        indent_levels.pop();
        return FeatureModelTypes.DEDENT;
    }
    else {
        yybegin(YYINITIAL);
    }
}

<feature>{FEATURENAME}+     {
        yybegin(indent);
        return FeatureModelTypes.FEATURENAME;
}
[^]    { return TokenType.BAD_CHARACTER; }
