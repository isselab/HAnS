// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.codeAnnotations.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static se.ch.HAnS.codeAnnotations.psi.CodeAnnotationTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class CodeAnnotationParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return marker(b, l + 1);
  }

  /* ********************************************************** */
  // BEGIN SPACE* parameter
  public static boolean beginmarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "beginmarker")) return false;
    if (!nextTokenIs(b, BEGIN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BEGIN);
    r = r && beginmarker_1(b, l + 1);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, BEGINMARKER, r);
    return r;
  }

  // SPACE*
  private static boolean beginmarker_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "beginmarker_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "beginmarker_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // END SPACE* parameter
  public static boolean endmarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endmarker")) return false;
    if (!nextTokenIs(b, END)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, END);
    r = r && endmarker_1(b, l + 1);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, ENDMARKER, r);
    return r;
  }

  // SPACE*
  private static boolean endmarker_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endmarker_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "endmarker_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LINE SPACE* parameter
  public static boolean linemarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "linemarker")) return false;
    if (!nextTokenIs(b, LINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LINE);
    r = r && linemarker_1(b, l + 1);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, LINEMARKER, r);
    return r;
  }

  // SPACE*
  private static boolean linemarker_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "linemarker_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "linemarker_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // FEATURENAME (SEPARATOR FEATURENAME)*
  public static boolean lpq(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FEATURENAME);
    r = r && lpq_1(b, l + 1);
    exit_section_(b, m, LPQ, r);
    return r;
  }

  // (SEPARATOR FEATURENAME)*
  private static boolean lpq_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!lpq_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lpq_1", c)) break;
    }
    return true;
  }

  // SEPARATOR FEATURENAME
  private static boolean lpq_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SEPARATOR, FEATURENAME);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // beginmarker|endmarker|linemarker
  static boolean marker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker")) return false;
    boolean r;
    r = beginmarker(b, l + 1);
    if (!r) r = endmarker(b, l + 1);
    if (!r) r = linemarker(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // OBRACKET SPACE* lpq ((SPACE* CS)? SPACE+ lpq)* SPACE* CBRACKET
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, OBRACKET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, OBRACKET);
    r = r && parameter_1(b, l + 1);
    r = r && lpq(b, l + 1);
    r = r && parameter_3(b, l + 1);
    r = r && parameter_4(b, l + 1);
    r = r && consumeToken(b, CBRACKET);
    exit_section_(b, m, PARAMETER, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_1", c)) break;
    }
    return true;
  }

  // ((SPACE* CS)? SPACE+ lpq)*
  private static boolean parameter_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter_3_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_3", c)) break;
    }
    return true;
  }

  // (SPACE* CS)? SPACE+ lpq
  private static boolean parameter_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_3_0_0(b, l + 1);
    r = r && parameter_3_0_1(b, l + 1);
    r = r && lpq(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SPACE* CS)?
  private static boolean parameter_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_0_0")) return false;
    parameter_3_0_0_0(b, l + 1);
    return true;
  }

  // SPACE* CS
  private static boolean parameter_3_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_3_0_0_0_0(b, l + 1);
    r = r && consumeToken(b, CS);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_3_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_0_0_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_3_0_0_0_0", c)) break;
    }
    return true;
  }

  // SPACE+
  private static boolean parameter_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_3_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SPACE);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_3_0_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_4", c)) break;
    }
    return true;
  }

}
