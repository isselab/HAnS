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
  // CRLF+ ((INDENT) feature*  DEDENT)?
  static boolean children_feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "children_feature")) return false;
    if (!nextTokenIs(b, CRLF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = children_feature_0(b, l + 1);
    r = r && children_feature_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+
  private static boolean children_feature_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "children_feature_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "children_feature_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ((INDENT) feature*  DEDENT)?
  private static boolean children_feature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "children_feature_1")) return false;
    children_feature_1_0(b, l + 1);
    return true;
  }

  // (INDENT) feature*  DEDENT
  private static boolean children_feature_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "children_feature_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INDENT);
    r = r && children_feature_1_0_1(b, l + 1);
    r = r && consumeToken(b, DEDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // feature*
  private static boolean children_feature_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "children_feature_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!feature(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "children_feature_1_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // FEATURENAME (SPACE OPTIONAL)? (group)? (children_feature)?
  public static boolean feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FEATURENAME);
    r = r && feature_1(b, l + 1);
    r = r && feature_2(b, l + 1);
    r = r && feature_3(b, l + 1);
    exit_section_(b, m, FEATURE, r);
    return r;
  }

  // (SPACE OPTIONAL)?
  private static boolean feature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1")) return false;
    feature_1_0(b, l + 1);
    return true;
  }

  // SPACE OPTIONAL
  private static boolean feature_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, SPACE, OPTIONAL);
    exit_section_(b, m, null, r);
    return r;
  }

  // (group)?
  private static boolean feature_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2")) return false;
    feature_2_0(b, l + 1);
    return true;
  }

  // (group)
  private static boolean feature_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (children_feature)?
  private static boolean feature_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3")) return false;
    feature_3_0(b, l + 1);
    return true;
  }

  // (children_feature)
  private static boolean feature_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = children_feature(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
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
  // CRLF+ (XOR_TOKEN | OR_TOKEN) (group_child|children_feature)
  static boolean group(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group")) return false;
    if (!nextTokenIs(b, CRLF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_0(b, l + 1);
    r = r && group_1(b, l + 1);
    r = r && group_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+
  private static boolean group_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "group_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // XOR_TOKEN | OR_TOKEN
  private static boolean group_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_1")) return false;
    boolean r;
    r = consumeToken(b, XOR_TOKEN);
    if (!r) r = consumeToken(b, OR_TOKEN);
    return r;
  }

  // group_child|children_feature
  private static boolean group_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_2")) return false;
    boolean r;
    r = group_child(b, l + 1);
    if (!r) r = children_feature(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // ((CRLF+ (INDENT))| SPACE)? feature* DEDENT
  static boolean group_child(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_child")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_child_0(b, l + 1);
    r = r && group_child_1(b, l + 1);
    r = r && consumeToken(b, DEDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((CRLF+ (INDENT))| SPACE)?
  private static boolean group_child_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_child_0")) return false;
    group_child_0_0(b, l + 1);
    return true;
  }

  // (CRLF+ (INDENT))| SPACE
  private static boolean group_child_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_child_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_child_0_0_0(b, l + 1);
    if (!r) r = consumeToken(b, SPACE);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+ (INDENT)
  private static boolean group_child_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_child_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_child_0_0_0_0(b, l + 1);
    r = r && consumeToken(b, INDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+
  private static boolean group_child_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_child_0_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "group_child_0_0_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // feature*
  private static boolean group_child_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_child_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!feature(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "group_child_1", c)) break;
    }
    return true;
  }

}
