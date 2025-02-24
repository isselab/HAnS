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
  // FEATURENAME (OPTIONAL)? (group? (children_feature)?)?
  public static boolean feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FEATURENAME);
    r = r && feature_1(b, l + 1);
    r = r && feature_2(b, l + 1);
    exit_section_(b, m, FEATURE, r);
    return r;
  }

  // (OPTIONAL)?
  private static boolean feature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1")) return false;
    consumeToken(b, OPTIONAL);
    return true;
  }

  // (group? (children_feature)?)?
  private static boolean feature_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2")) return false;
    feature_2_0(b, l + 1);
    return true;
  }

  // group? (children_feature)?
  private static boolean feature_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature_2_0_0(b, l + 1);
    r = r && feature_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // group?
  private static boolean feature_2_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2_0_0")) return false;
    group(b, l + 1);
    return true;
  }

  // (children_feature)?
  private static boolean feature_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2_0_1")) return false;
    feature_2_0_1_0(b, l + 1);
    return true;
  }

  // (children_feature)
  private static boolean feature_2_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2_0_1_0")) return false;
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
  // CRLF+ (OR_TOKEN | XOR_TOKEN) FEATURENAME
  static boolean group(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group")) return false;
    if (!nextTokenIs(b, CRLF)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = group_0(b, l + 1);
    r = r && group_1(b, l + 1);
    r = r && consumeToken(b, FEATURENAME);
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

  // OR_TOKEN | XOR_TOKEN
  private static boolean group_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "group_1")) return false;
    boolean r;
    r = consumeToken(b, OR_TOKEN);
    if (!r) r = consumeToken(b, XOR_TOKEN);
    return r;
  }

}
