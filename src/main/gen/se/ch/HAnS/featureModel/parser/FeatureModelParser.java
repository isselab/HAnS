// This is a generated file. Not intended for manual editing.
package se.ch.HAnS.featureModel.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static se.ch.HAnS.featureModel.psi.FeatureModelTypes.*;
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
  // FEATURENAME | '\t' feature
  public static boolean feature(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FEATURE, "<feature>");
    r = consumeToken(b, FEATURENAME);
    if (!r) r = feature_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // '\t' feature
  private static boolean feature_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "feature_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, "\\t");
    r = r && feature(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // projectName ((CRLF)* feature)* (('\t'|CRLF)*)
  static boolean featureModelFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = projectName(b, l + 1);
    r = r && featureModelFile_1(b, l + 1);
    r = r && featureModelFile_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ((CRLF)* feature)*
  private static boolean featureModelFile_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!featureModelFile_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "featureModelFile_1", c)) break;
    }
    return true;
  }

  // (CRLF)* feature
  private static boolean featureModelFile_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = featureModelFile_1_0_0(b, l + 1);
    r = r && feature(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (CRLF)*
  private static boolean featureModelFile_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_1_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, CRLF)) break;
      if (!empty_element_parsed_guard_(b, "featureModelFile_1_0_0", c)) break;
    }
    return true;
  }

  // ('\t'|CRLF)*
  private static boolean featureModelFile_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!featureModelFile_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "featureModelFile_2", c)) break;
    }
    return true;
  }

  // '\t'|CRLF
  private static boolean featureModelFile_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureModelFile_2_0")) return false;
    boolean r;
    r = consumeToken(b, "\\t");
    if (!r) r = consumeToken(b, CRLF);
    return r;
  }

  /* ********************************************************** */
  // FEATURENAME
  public static boolean projectName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "projectName")) return false;
    if (!nextTokenIs(b, FEATURENAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FEATURENAME);
    exit_section_(b, m, PROJECT_NAME, r);
    return r;
  }

}
