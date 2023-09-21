// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.codeAnnotation.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static se.isselab.HAnS.codeAnnotation.psi.CodeAnnotationTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
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
  // BEGIN parameter
  public static boolean beginmarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "beginmarker")) return false;
    if (!nextTokenIs(b, BEGIN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BEGIN);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, BEGINMARKER, r);
    return r;
  }

  /* ********************************************************** */
  // END parameter
  public static boolean endmarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "endmarker")) return false;
    if (!nextTokenIs(b, END)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, END);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, ENDMARKER, r);
    return r;
  }

  /* ********************************************************** */
  // FEATURENAME
  public static boolean feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FEATURENAME);
    exit_section_(b, m, FEATURE, r);
    return r;
  }

  /* ********************************************************** */
  // LINE parameter
  public static boolean linemarker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "linemarker")) return false;
    if (!nextTokenIs(b, LINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LINE);
    r = r && parameter(b, l + 1);
    exit_section_(b, m, LINEMARKER, r);
    return r;
  }

  /* ********************************************************** */
  // feature (SEPARATOR feature)*
  public static boolean lpq(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature(b, l + 1);
    r = r && lpq_1(b, l + 1);
    exit_section_(b, m, LPQ, r);
    return r;
  }

  // (SEPARATOR feature)*
  private static boolean lpq_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!lpq_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lpq_1", c)) break;
    }
    return true;
  }

  // SEPARATOR feature
  private static boolean lpq_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEPARATOR);
    r = r && feature(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (COMMENTMARKER|SPACE)* (beginmarker|endmarker|linemarker) (SPACE|COMMENTMARKER)*
  static boolean marker(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = marker_0(b, l + 1);
    r = r && marker_1(b, l + 1);
    r = r && marker_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COMMENTMARKER|SPACE)*
  private static boolean marker_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!marker_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "marker_0", c)) break;
    }
    return true;
  }

  // COMMENTMARKER|SPACE
  private static boolean marker_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker_0_0")) return false;
    boolean r;
    r = consumeToken(b, COMMENTMARKER);
    if (!r) r = consumeToken(b, SPACE);
    return r;
  }

  // beginmarker|endmarker|linemarker
  private static boolean marker_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker_1")) return false;
    boolean r;
    r = beginmarker(b, l + 1);
    if (!r) r = endmarker(b, l + 1);
    if (!r) r = linemarker(b, l + 1);
    return r;
  }

  // (SPACE|COMMENTMARKER)*
  private static boolean marker_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!marker_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "marker_2", c)) break;
    }
    return true;
  }

  // SPACE|COMMENTMARKER
  private static boolean marker_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "marker_2_0")) return false;
    boolean r;
    r = consumeToken(b, SPACE);
    if (!r) r = consumeToken(b, COMMENTMARKER);
    return r;
  }

  /* ********************************************************** */
  // (SPACE* OBRACKET SPACE* lpq ((SPACE* CS)? SPACE+ lpq)* SPACE* CBRACKET) | (SPACE+ lpq ((SPACE* CS)? SPACE+ lpq)* SPACE*)
  public static boolean parameter(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter")) return false;
    if (!nextTokenIs(b, "<parameter>", OBRACKET, SPACE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PARAMETER, "<parameter>");
    r = parameter_0(b, l + 1);
    if (!r) r = parameter_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // SPACE* OBRACKET SPACE* lpq ((SPACE* CS)? SPACE+ lpq)* SPACE* CBRACKET
  private static boolean parameter_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_0(b, l + 1);
    r = r && consumeToken(b, OBRACKET);
    r = r && parameter_0_2(b, l + 1);
    r = r && lpq(b, l + 1);
    r = r && parameter_0_4(b, l + 1);
    r = r && parameter_0_5(b, l + 1);
    r = r && consumeToken(b, CBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_0_0", c)) break;
    }
    return true;
  }

  // SPACE*
  private static boolean parameter_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_0_2", c)) break;
    }
    return true;
  }

  // ((SPACE* CS)? SPACE+ lpq)*
  private static boolean parameter_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter_0_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_0_4", c)) break;
    }
    return true;
  }

  // (SPACE* CS)? SPACE+ lpq
  private static boolean parameter_0_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_4_0_0(b, l + 1);
    r = r && parameter_0_4_0_1(b, l + 1);
    r = r && lpq(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SPACE* CS)?
  private static boolean parameter_0_4_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4_0_0")) return false;
    parameter_0_4_0_0_0(b, l + 1);
    return true;
  }

  // SPACE* CS
  private static boolean parameter_0_4_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_0_4_0_0_0_0(b, l + 1);
    r = r && consumeToken(b, CS);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_0_4_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4_0_0_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_0_4_0_0_0_0", c)) break;
    }
    return true;
  }

  // SPACE+
  private static boolean parameter_0_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_4_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SPACE);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_0_4_0_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_0_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_0_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_0_5", c)) break;
    }
    return true;
  }

  // SPACE+ lpq ((SPACE* CS)? SPACE+ lpq)* SPACE*
  private static boolean parameter_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_1_0(b, l + 1);
    r = r && lpq(b, l + 1);
    r = r && parameter_1_2(b, l + 1);
    r = r && parameter_1_3(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE+
  private static boolean parameter_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SPACE);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_1_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ((SPACE* CS)? SPACE+ lpq)*
  private static boolean parameter_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!parameter_1_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "parameter_1_2", c)) break;
    }
    return true;
  }

  // (SPACE* CS)? SPACE+ lpq
  private static boolean parameter_1_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_1_2_0_0(b, l + 1);
    r = r && parameter_1_2_0_1(b, l + 1);
    r = r && lpq(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SPACE* CS)?
  private static boolean parameter_1_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_2_0_0")) return false;
    parameter_1_2_0_0_0(b, l + 1);
    return true;
  }

  // SPACE* CS
  private static boolean parameter_1_2_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_2_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = parameter_1_2_0_0_0_0(b, l + 1);
    r = r && consumeToken(b, CS);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_1_2_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_2_0_0_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_1_2_0_0_0_0", c)) break;
    }
    return true;
  }

  // SPACE+
  private static boolean parameter_1_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_2_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SPACE);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_1_2_0_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean parameter_1_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "parameter_1_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "parameter_1_3", c)) break;
    }
    return true;
  }

}
