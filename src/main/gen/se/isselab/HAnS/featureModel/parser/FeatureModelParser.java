// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.featureModel.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static se.isselab.HAnS.featureModel.psi.FeatureModelTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class FeatureModelParser implements PsiParser, LightPsiParser {

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
    return featureModelFile(b, l + 1);
  }

  /* ********************************************************** */
  // FEATURENAME // Simplified for now
  //                       | "!" booleanExpression
  //                       | booleanExpression "&&" booleanExpression
  //                       | booleanExpression "||" booleanExpression
  //                       | booleanExpression "=>" booleanExpression
  //                       | "(" booleanExpression ")"
  public static boolean booleanExpression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanExpression")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, BOOLEAN_EXPRESSION, "<boolean expression>");
    r = consumeToken(b, FEATURENAME);
    if (!r) r = booleanExpression_1(b, l + 1);
    if (!r) r = booleanExpression_2(b, l + 1);
    if (!r) r = booleanExpression_3(b, l + 1);
    if (!r) r = booleanExpression_4(b, l + 1);
    if (!r) r = booleanExpression_5(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // "!" booleanExpression
  private static boolean booleanExpression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanExpression_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "!");
    r = r && booleanExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // booleanExpression "&&" booleanExpression
  private static boolean booleanExpression_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanExpression_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = booleanExpression(b, l + 1);
    r = r && consumeToken(b, "&&");
    r = r && booleanExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // booleanExpression "||" booleanExpression
  private static boolean booleanExpression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanExpression_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = booleanExpression(b, l + 1);
    r = r && consumeToken(b, "||");
    r = r && booleanExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // booleanExpression "=>" booleanExpression
  private static boolean booleanExpression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanExpression_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = booleanExpression(b, l + 1);
    r = r && consumeToken(b, "=>");
    r = r && booleanExpression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // "(" booleanExpression ")"
  private static boolean booleanExpression_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "booleanExpression_5")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "(");
    r = r && booleanExpression(b, l + 1);
    r = r && consumeToken(b, ")");
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // "[" booleanExpression "]"
  public static boolean constraint(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "constraint")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONSTRAINT, "<constraint>");
    r = consumeToken(b, "[");
    r = r && booleanExpression(b, l + 1);
    r = r && consumeToken(b, "]");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // FEATURENAME (CRLF+ ((INDENT) feature* DEDENT)?)?
  public static boolean feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FEATURENAME);
    r = r && feature_1(b, l + 1);
    exit_section_(b, m, FEATURE, r);
    return r;
  }

  // (CRLF+ ((INDENT) feature* DEDENT)?)?
  private static boolean feature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1")) return false;
    feature_1_0(b, l + 1);
    return true;
  }

  // CRLF+ ((INDENT) feature* DEDENT)?
  private static boolean feature_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature_1_0_0(b, l + 1);
    r = r && feature_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+
  private static boolean feature_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "feature_1_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ((INDENT) feature* DEDENT)?
  private static boolean feature_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1_0_1")) return false;
    feature_1_0_1_0(b, l + 1);
    return true;
  }

  // (INDENT) feature* DEDENT
  private static boolean feature_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INDENT);
    r = r && feature_1_0_1_0_1(b, l + 1);
    r = r && consumeToken(b, DEDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // feature*
  private static boolean feature_1_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!feature(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "feature_1_0_1_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (feature (feature)?)?
  static boolean featureModelFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile")) return false;
    featureModelFile_0(b, l + 1);
    return true;
  }

  // feature (feature)?
  private static boolean featureModelFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature(b, l + 1);
    r = r && featureModelFile_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (feature)?
  private static boolean featureModelFile_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_0_1")) return false;
    featureModelFile_0_1_0(b, l + 1);
    return true;
  }

  // (feature)
  private static boolean featureModelFile_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // feature | orGroup | xorGroup
  public static boolean featureOrGroup(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureOrGroup")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FEATURE_OR_GROUP, "<feature or group>");
    r = feature(b, l + 1);
    if (!r) r = orGroup(b, l + 1);
    if (!r) r = xorGroup(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // "?"
  public static boolean optionality(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "optionality")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OPTIONALITY, "<optionality>");
    r = consumeToken(b, "?");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // "or" CRLF+ INDENT feature+ DEDENT
  public static boolean orGroup(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orGroup")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, OR_GROUP, "<or group>");
    r = consumeToken(b, "or");
    r = r && orGroup_1(b, l + 1);
    r = r && consumeToken(b, INDENT);
    r = r && orGroup_3(b, l + 1);
    r = r && consumeToken(b, DEDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CRLF+
  private static boolean orGroup_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orGroup_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "orGroup_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // feature+
  private static boolean orGroup_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "orGroup_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!feature(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "orGroup_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // "xor" CRLF+ INDENT feature+ DEDENT
  public static boolean xorGroup(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "xorGroup")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, XOR_GROUP, "<xor group>");
    r = consumeToken(b, "xor");
    r = r && xorGroup_1(b, l + 1);
    r = r && consumeToken(b, INDENT);
    r = r && xorGroup_3(b, l + 1);
    r = r && consumeToken(b, DEDENT);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CRLF+
  private static boolean xorGroup_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "xorGroup_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "xorGroup_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // feature+
  private static boolean xorGroup_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "xorGroup_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!feature(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "xorGroup_3", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

}
