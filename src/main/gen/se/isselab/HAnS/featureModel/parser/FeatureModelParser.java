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
  // BRACKATSOPEN FEATURE1 ARROW FEATURE1 BRACKATSCLOSE (CRLF+ (cross_constrains)?)*
  public static boolean cross_constrains(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cross_constrains")) return false;
    if (!nextTokenIs(b, BRACKATSOPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, BRACKATSOPEN, FEATURE1, ARROW, FEATURE1, BRACKATSCLOSE);
    r = r && cross_constrains_5(b, l + 1);
    exit_section_(b, m, CROSS_CONSTRAINS, r);
    return r;
  }

  // (CRLF+ (cross_constrains)?)*
  private static boolean cross_constrains_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cross_constrains_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!cross_constrains_5_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "cross_constrains_5", c)) break;
    }
    return true;
  }

  // CRLF+ (cross_constrains)?
  private static boolean cross_constrains_5_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cross_constrains_5_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = cross_constrains_5_0_0(b, l + 1);
    r = r && cross_constrains_5_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+
  private static boolean cross_constrains_5_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cross_constrains_5_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "cross_constrains_5_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // (cross_constrains)?
  private static boolean cross_constrains_5_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cross_constrains_5_0_1")) return false;
    cross_constrains_5_0_1_0(b, l + 1);
    return true;
  }

  // (cross_constrains)
  private static boolean cross_constrains_5_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cross_constrains_5_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = cross_constrains(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (OR | XOR)? (FEATURENAME) (OPTIONAL)? (CRLF+ ((INDENT) feature* DEDENT)?)? (cross_constrains)?
  public static boolean feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FEATURE, "<feature>");
    r = feature_0(b, l + 1);
    r = r && consumeToken(b, FEATURENAME);
    r = r && feature_2(b, l + 1);
    r = r && feature_3(b, l + 1);
    r = r && feature_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (OR | XOR)?
  private static boolean feature_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_0")) return false;
    feature_0_0(b, l + 1);
    return true;
  }

  // OR | XOR
  private static boolean feature_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_0_0")) return false;
    boolean r;
    r = consumeToken(b, OR);
    if (!r) r = consumeToken(b, XOR);
    return r;
  }

  // (OPTIONAL)?
  private static boolean feature_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_2")) return false;
    consumeToken(b, OPTIONAL);
    return true;
  }

  // (CRLF+ ((INDENT) feature* DEDENT)?)?
  private static boolean feature_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3")) return false;
    feature_3_0(b, l + 1);
    return true;
  }

  // CRLF+ ((INDENT) feature* DEDENT)?
  private static boolean feature_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = feature_3_0_0(b, l + 1);
    r = r && feature_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // CRLF+
  private static boolean feature_3_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CRLF);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "feature_3_0_0", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // ((INDENT) feature* DEDENT)?
  private static boolean feature_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3_0_1")) return false;
    feature_3_0_1_0(b, l + 1);
    return true;
  }

  // (INDENT) feature* DEDENT
  private static boolean feature_3_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INDENT);
    r = r && feature_3_0_1_0_1(b, l + 1);
    r = r && consumeToken(b, DEDENT);
    exit_section_(b, m, null, r);
    return r;
  }

  // feature*
  private static boolean feature_3_0_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_3_0_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!feature(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "feature_3_0_1_0_1", c)) break;
    }
    return true;
  }

  // (cross_constrains)?
  private static boolean feature_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_4")) return false;
    feature_4_0(b, l + 1);
    return true;
  }

  // (cross_constrains)
  private static boolean feature_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = cross_constrains(b, l + 1);
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

}
