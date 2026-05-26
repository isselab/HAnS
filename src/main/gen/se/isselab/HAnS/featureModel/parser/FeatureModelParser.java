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

  public ASTNode parse(IElementType root_, PsiBuilder builder_) {
    parseLight(root_, builder_);
    return builder_.getTreeBuilt();
  }

  public void parseLight(IElementType root_, PsiBuilder builder_) {
    boolean result_;
    builder_ = adapt_builder_(root_, builder_, this, null);
    Marker marker_ = enter_section_(builder_, 0, _COLLAPSE_, null);
    result_ = parse_root_(root_, builder_);
    exit_section_(builder_, 0, marker_, root_, result_, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType root_, PsiBuilder builder_) {
    return parse_root_(root_, builder_, 0);
  }

  static boolean parse_root_(IElementType root_, PsiBuilder builder_, int level_) {
    return featureModelFile(builder_, level_ + 1);
  }

  /* ********************************************************** */
  // feature | LOGIC
  public static boolean COMPONENT(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "COMPONENT")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, COMPONENT, "<component>");
    result_ = feature(builder_, level_ + 1);
    if (!result_) result_ = LOGIC(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  /* ********************************************************** */
  // (OR_BLOCK) | (XOR_BLOCK)
  public static boolean LOGIC(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LOGIC")) return false;
    if (!nextTokenIs(builder_, "<logic>", OR, XOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_, level_, _NONE_, LOGIC, "<logic>");
    result_ = LOGIC_0(builder_, level_ + 1);
    if (!result_) result_ = LOGIC_1(builder_, level_ + 1);
    exit_section_(builder_, level_, marker_, result_, false, null);
    return result_;
  }

  // (OR_BLOCK)
  private static boolean LOGIC_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LOGIC_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = OR_BLOCK(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (XOR_BLOCK)
  private static boolean LOGIC_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "LOGIC_1")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = XOR_BLOCK(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // OR CRLF* (SUB_LOGIC)
  public static boolean OR_BLOCK(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "OR_BLOCK")) return false;
    if (!nextTokenIs(builder_, OR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, OR);
    result_ = result_ && OR_BLOCK_1(builder_, level_ + 1);
    result_ = result_ && OR_BLOCK_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, OR_BLOCK, result_);
    return result_;
  }

  // CRLF*
  private static boolean OR_BLOCK_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "OR_BLOCK_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!consumeToken(builder_, CRLF)) break;
      if (!empty_element_parsed_guard_(builder_, "OR_BLOCK_1", pos_)) break;
    }
    return true;
  }

  // (SUB_LOGIC)
  private static boolean OR_BLOCK_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "OR_BLOCK_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = SUB_LOGIC(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // INDENT COMPONENT (COMPONENT)+ DEDENT
  public static boolean SUB_LOGIC(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SUB_LOGIC")) return false;
    if (!nextTokenIs(builder_, INDENT)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, INDENT);
    result_ = result_ && COMPONENT(builder_, level_ + 1);
    result_ = result_ && SUB_LOGIC_2(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DEDENT);
    exit_section_(builder_, marker_, SUB_LOGIC, result_);
    return result_;
  }

  // (COMPONENT)+
  private static boolean SUB_LOGIC_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SUB_LOGIC_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = SUB_LOGIC_2_0(builder_, level_ + 1);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!SUB_LOGIC_2_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "SUB_LOGIC_2", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMPONENT)
  private static boolean SUB_LOGIC_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "SUB_LOGIC_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = COMPONENT(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // XOR CRLF* (SUB_LOGIC)
  public static boolean XOR_BLOCK(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "XOR_BLOCK")) return false;
    if (!nextTokenIs(builder_, XOR)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, XOR);
    result_ = result_ && XOR_BLOCK_1(builder_, level_ + 1);
    result_ = result_ && XOR_BLOCK_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, XOR_BLOCK, result_);
    return result_;
  }

  // CRLF*
  private static boolean XOR_BLOCK_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "XOR_BLOCK_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!consumeToken(builder_, CRLF)) break;
      if (!empty_element_parsed_guard_(builder_, "XOR_BLOCK_1", pos_)) break;
    }
    return true;
  }

  // (SUB_LOGIC)
  private static boolean XOR_BLOCK_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "XOR_BLOCK_2")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = SUB_LOGIC(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // FEATURENAME (QUESTIONMARK)? (CRLF+ ((INDENT) (COMPONENT)* DEDENT)?)?
  public static boolean feature(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature")) return false;
    if (!nextTokenIs(builder_, FEATURENAME)) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, FEATURENAME);
    result_ = result_ && feature_1(builder_, level_ + 1);
    result_ = result_ && feature_2(builder_, level_ + 1);
    exit_section_(builder_, marker_, FEATURE, result_);
    return result_;
  }

  // (QUESTIONMARK)?
  private static boolean feature_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_1")) return false;
    consumeToken(builder_, QUESTIONMARK);
    return true;
  }

  // (CRLF+ ((INDENT) (COMPONENT)* DEDENT)?)?
  private static boolean feature_2(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2")) return false;
    feature_2_0(builder_, level_ + 1);
    return true;
  }

  // CRLF+ ((INDENT) (COMPONENT)* DEDENT)?
  private static boolean feature_2_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = feature_2_0_0(builder_, level_ + 1);
    result_ = result_ && feature_2_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // CRLF+
  private static boolean feature_2_0_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2_0_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, CRLF);
    while (result_) {
      int pos_ = current_position_(builder_);
      if (!consumeToken(builder_, CRLF)) break;
      if (!empty_element_parsed_guard_(builder_, "feature_2_0_0", pos_)) break;
    }
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // ((INDENT) (COMPONENT)* DEDENT)?
  private static boolean feature_2_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2_0_1")) return false;
    feature_2_0_1_0(builder_, level_ + 1);
    return true;
  }

  // (INDENT) (COMPONENT)* DEDENT
  private static boolean feature_2_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = consumeToken(builder_, INDENT);
    result_ = result_ && feature_2_0_1_0_1(builder_, level_ + 1);
    result_ = result_ && consumeToken(builder_, DEDENT);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (COMPONENT)*
  private static boolean feature_2_0_1_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2_0_1_0_1")) return false;
    while (true) {
      int pos_ = current_position_(builder_);
      if (!feature_2_0_1_0_1_0(builder_, level_ + 1)) break;
      if (!empty_element_parsed_guard_(builder_, "feature_2_0_1_0_1", pos_)) break;
    }
    return true;
  }

  // (COMPONENT)
  private static boolean feature_2_0_1_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "feature_2_0_1_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = COMPONENT(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  /* ********************************************************** */
  // (feature (feature)?)?
  static boolean featureModelFile(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "featureModelFile")) return false;
    featureModelFile_0(builder_, level_ + 1);
    return true;
  }

  // feature (feature)?
  private static boolean featureModelFile_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "featureModelFile_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = feature(builder_, level_ + 1);
    result_ = result_ && featureModelFile_0_1(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

  // (feature)?
  private static boolean featureModelFile_0_1(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "featureModelFile_0_1")) return false;
    featureModelFile_0_1_0(builder_, level_ + 1);
    return true;
  }

  // (feature)
  private static boolean featureModelFile_0_1_0(PsiBuilder builder_, int level_) {
    if (!recursion_guard_(builder_, level_, "featureModelFile_0_1_0")) return false;
    boolean result_;
    Marker marker_ = enter_section_(builder_);
    result_ = feature(builder_, level_ + 1);
    exit_section_(builder_, marker_, null, result_);
    return result_;
  }

}
