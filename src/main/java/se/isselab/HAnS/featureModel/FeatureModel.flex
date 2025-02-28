/*
Copyright 2021 Herman Jansson & Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

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
QUESTIONMARK = ['?']
OR = 'OR'
XOR = ['xor']

INDENT=[\t]

FEATURENAME= [[A-Z]+|[a-z]+|[0-9]+|'_'+|'\''+]

%{
    int current_line_indent = 0;
        int indent_level = 0;
        int indent_caller = indent;

        final int TAB_WIDTH = 4;

        int test = 1;

        Deque<Integer> indent_levels = new ArrayDeque<Integer>();
        private IElementType goIntoFeaturename() {
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
            return null;
        }
        private IElementType goToDedent(int nextStae) {
            indent_levels.pop();
            if(current_line_indent != indent_levels.peek()) {
                yypushback(1);
                return FeatureModelTypes.DEDENT;
            }
            else {
                yypushback(1);
                yybegin(nextStae);
                return FeatureModelTypes.DEDENT;
            }
        }
        private void print(String str) {System.out.println(str);}
%}

%x indent
%s feature
%s dedent
%s questionmark
%s or1
%s or2
%s xor1
%s xor2
%s xor3
%s returFeaturename
%s returnOr
%s returnOrDedent
%s returnXor
%s returnXorDedent

%%
<YYINITIAL>.         { yypushback(1); indent_levels.push(0); yybegin(feature); }

<indent>{SPACE}      { current_line_indent++; }

<indent>{INDENT}     { current_line_indent = (current_line_indent + TAB_WIDTH) & ~(TAB_WIDTH-1); }
<indent>{CRLF}+      { current_line_indent = 0; return FeatureModelTypes.CRLF; }

<returFeaturename>[^] {
          yybegin(indent);
          return FeatureModelTypes.FEATURENAME;
      }
<returnOr> [^] {
          yypushback(1);
          yybegin(indent);
          current_line_indent += 2;
          return FeatureModelTypes.OR;
      }
<returnOrDedent> [^] {
          return goToDedent(returnOr);
      }
<returnXor> [^] {
          yypushback(1);
          yybegin(indent);
          current_line_indent += 3;
          return FeatureModelTypes.XOR;
      }
<returnXorDedent> [^] {
          return goToDedent(returnXor);
      }

<indent>{QUESTIONMARK} {
          yybegin(indent);
          return FeatureModelTypes.QUESTIONMARK;
      }
<indent>"o" {
          yybegin(or1);
      }
<or1>"r" {
          yybegin(or2);
      }
<or1>({CRLF}|{INDENT}) {
          if (current_line_indent > indent_levels.peek()) {
                indent_levels.push(current_line_indent);
                yypushback(1);
                yybegin(returFeaturename);
                return FeatureModelTypes.INDENT;
            }
            yybegin(indent);
            return FeatureModelTypes.FEATURENAME;
      }
<or1>[^] {
          IElementType result = goIntoFeaturename();
          if (result != null) return result;
      }

<or2>{FEATURENAME} {
          IElementType result = goIntoFeaturename();
          if (result != null) return result;
      }
<or2>[^] {
          if(current_line_indent > indent_levels.peek()) {
              indent_levels.push(current_line_indent);
              yypushback(1);
              yybegin(returnOr);
              return FeatureModelTypes.INDENT;
          }
          else if(current_line_indent < indent_levels.peek()) {
              indent_levels.pop();
              if (current_line_indent > indent_levels.peek()) {
                  indent_levels.push(current_line_indent);
                  yypushback(1);
                  yybegin(returnOr);
              }
              else if(current_line_indent != indent_levels.peek()) {
                  yypushback(1);
                  yybegin(returnOrDedent);
                  return FeatureModelTypes.DEDENT;
              }
              else {
                  yypushback(1);
                  yybegin(returnOr);
                  return FeatureModelTypes.DEDENT;
              }
          }
          else if (current_line_indent == 0){
              yypushback(1);
              yybegin(returnOr);
          }
          else {
              yypushback(1);
              yybegin(returnOr);
              return FeatureModelTypes.CRLF;
          }
      }

<indent>"x" {
          yybegin(xor1);
      }
<xor1>"o" {
          yybegin(xor2);
      }
<xor1>({CRLF}|{INDENT}) {
          if (current_line_indent > indent_levels.peek()) {
              indent_levels.push(current_line_indent);
              yypushback(1);
              yybegin(returFeaturename);
              return FeatureModelTypes.INDENT;
          }
          yybegin(indent);
          return FeatureModelTypes.FEATURENAME;
      }
<xor1>[^] {
          IElementType result = goIntoFeaturename();
          if (result != null) return result;
      }

<xor2>"r" {
          yybegin(xor3);
      }
<xor2>({CRLF}|{INDENT}) {
          if (current_line_indent > indent_levels.peek()) {
                indent_levels.push(current_line_indent);
                yypushback(1);
                yybegin(returFeaturename);
                return FeatureModelTypes.INDENT;
            }
            yybegin(indent);
            return FeatureModelTypes.FEATURENAME;
      }
<xor2>[^] {
          IElementType result = goIntoFeaturename();
          if (result != null) return result;
      }
<xor3>{FEATURENAME} {
          IElementType result = goIntoFeaturename();
          if (result != null) return result;
      }
<xor3>[^] {
          if(current_line_indent > indent_levels.peek()) {
                indent_levels.push(current_line_indent);
                yypushback(1);
                yybegin(returnXor);
                return FeatureModelTypes.INDENT;
            }
            else if(current_line_indent < indent_levels.peek()) {
                indent_levels.pop();
                if (current_line_indent > indent_levels.peek()) {
                    indent_levels.push(current_line_indent);
                    yypushback(1);
                    yybegin(returnXor);
                }
                else if(current_line_indent != indent_levels.peek()) {
                    yypushback(1);
                    yybegin(returnXorDedent);
                    return FeatureModelTypes.DEDENT;
                }
                else {
                    yypushback(1);
                    yybegin(returnXor);
                    return FeatureModelTypes.DEDENT;
                }
            }
            else if (current_line_indent == 0){
                yypushback(1);
                yybegin(returnXor);
            }
            else {
                yypushback(1);
                yybegin(returnXor);
                return FeatureModelTypes.CRLF;
            }
      }


<dedent>. {
    return goToDedent(feature);
}

<indent>{FEATURENAME}       {
        IElementType result = goIntoFeaturename();
          if (result != null) return result;
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
<or2><<EOF>> {
          yybegin(indent);
          return FeatureModelTypes.OR;
      }
<xor3><<EOF>> {
          yybegin(indent);
          return FeatureModelTypes.XOR;
      }

<or1, xor1, xor2><<EOF>> {
          if (current_line_indent > indent_levels.peek()) {
            indent_levels.push(current_line_indent);
            yybegin(returFeaturename);
            return FeatureModelTypes.INDENT;
          }
          yybegin(indent);
          return FeatureModelTypes.FEATURENAME;
      }
<returFeaturename><<EOF>> {
          yybegin(indent);
          return FeatureModelTypes.FEATURENAME;
      }


<feature>{FEATURENAME}+     {
        yybegin(indent);
        return FeatureModelTypes.FEATURENAME;
}
[^]    {
          return TokenType.BAD_CHARACTER;
      }

