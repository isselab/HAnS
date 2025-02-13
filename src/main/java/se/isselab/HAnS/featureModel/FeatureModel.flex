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
    private IElementType goIntoFeaturname() {
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

%%
<YYINITIAL>.         { yypushback(1); indent_levels.push(0); yybegin(feature); }

<indent>{SPACE}      { current_line_indent++; }

<indent>{INDENT}     { current_line_indent = (current_line_indent + TAB_WIDTH) & ~(TAB_WIDTH-1); }
<indent>{CRLF}+      { current_line_indent = 0; return FeatureModelTypes.CRLF; }

<returFeaturename>[^] {
          yybegin(indent);
          return FeatureModelTypes.FEATURENAME;
      }

<indent>{QUESTIONMARK} {
          //System.out.println("Found questionmark\nreturning Question token");
          yybegin(indent);
          return FeatureModelTypes.QUESTIONMARK;
      }
<indent>"o" {
          System.out.println("indent: Found o");
          yybegin(or1);
      }
<or1>"r" {
          System.out.println("or1: Found r");
          yybegin(or2);
      }
<or1>({CRLF}|{INDENT}) {
          System.out.println("or1: found crlf");
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
          System.out.println("or1: Did not find r");
          IElementType result = goIntoFeaturname();
          if (result != null) return result;
      }

<or2>{FEATURENAME} {
          print("or2: found featurename");
          IElementType result = goIntoFeaturname();
          if (result != null) return result;
      }
<or2>[^] {
          System.out.println("Returning OR");
          yypushback(1);
          yybegin(indent);
          return FeatureModelTypes.OR;
      }

<indent>"x" {
          print("indent: Found x");
          yybegin(xor1);
      }
<xor1>"o" {
          print("xor1: Found o");
          yybegin(xor2);
      }
<xor1>({CRLF}|{INDENT}) {
          print("xor1: CRLF");
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
          System.out.println("xor1: Did not find o");
          IElementType result = goIntoFeaturname();
          if (result != null) return result;
      }

<xor2>"r" {
          print("xor2: Found r");
          yybegin(xor3);
      }
<xor2>({CRLF}|{INDENT}) {
          print("xor2: found CRLF");
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
          print("xor2: Did not find r");
          IElementType result = goIntoFeaturname();
          if (result != null) return result;
      }
<xor3>{FEATURENAME} {
          print("xor3: got featurename: " + yytext());
          IElementType result = goIntoFeaturname();
          if (result != null) return result;
      }
<xor3>[^] {
          System.out.println("Returning XOR");
          yypushback(1);
          yybegin(indent);
          return FeatureModelTypes.XOR;
      }


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
        IElementType result = goIntoFeaturname();
          if (result != null) return result;
}

<indent><<EOF>> {
          System.out.println("EOF in indent");
    if (indent_levels.peek() != 0) {
        indent_levels.pop();
        return FeatureModelTypes.DEDENT;
    }
    else {
        yybegin(YYINITIAL);
    }
}
<or2><<EOF>> {
          System.out.println("EOF in or2");
          yybegin(indent);
          return FeatureModelTypes.OR;
      }
<xor3><<EOF>> {
          System.out.println("EOF in xor3");
          yybegin(indent);
          return FeatureModelTypes.XOR;
      }

<or1, xor1, xor2><<EOF>> {
          print("EOF in or1,xor1,xor2");
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
          System.out.println("Got a bad character: \"" + yytext() + "\" in state " + yystate());
          return TokenType.BAD_CHARACTER;
      }

