// This is a generated file. Not intended for manual editing.
package se.isselab.HAnS.fileAnnotation.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static se.isselab.HAnS.fileAnnotation.psi.FileAnnotationTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class FileAnnotationParser implements PsiParser, LightPsiParser {

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
    return fileAnnotationFile(b, l + 1);
  }

  /* ********************************************************** */
  // STRING
  public static boolean featureName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "featureName")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, STRING);
    exit_section_(b, m, FEATURE_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // fileReferences COLON? NEWLINE+ lpqReferences
  public static boolean fileAnnotation(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileAnnotation")) return false;
    if (!nextTokenIs(b, "<file annotation>", CM, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILE_ANNOTATION, "<file annotation>");
    r = fileReferences(b, l + 1);
    r = r && fileAnnotation_1(b, l + 1);
    r = r && fileAnnotation_2(b, l + 1);
    r = r && lpqReferences(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // COLON?
  private static boolean fileAnnotation_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileAnnotation_1")) return false;
    consumeToken(b, COLON);
    return true;
  }

  // NEWLINE+
  private static boolean fileAnnotation_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileAnnotation_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NEWLINE);
    while (r) {
      int c = current_position_(b);
      if (!consumeToken(b, NEWLINE)) break;
      if (!empty_element_parsed_guard_(b, "fileAnnotation_2", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (fileAnnotation NEWLINE*)*
  static boolean fileAnnotationFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileAnnotationFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!fileAnnotationFile_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "fileAnnotationFile", c)) break;
    }
    return true;
  }

  // fileAnnotation NEWLINE*
  private static boolean fileAnnotationFile_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileAnnotationFile_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fileAnnotation(b, l + 1);
    r = r && fileAnnotationFile_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NEWLINE*
  private static boolean fileAnnotationFile_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileAnnotationFile_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, NEWLINE)) break;
      if (!empty_element_parsed_guard_(b, "fileAnnotationFile_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (STRING DOT STRING)|STRING
  public static boolean fileName(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileName")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fileName_0(b, l + 1);
    if (!r) r = consumeToken(b, STRING);
    exit_section_(b, m, FILE_NAME, r);
    return r;
  }

  // STRING DOT STRING
  private static boolean fileName_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileName_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, STRING, DOT, STRING);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // (CM fileName CM) | (fileName)
  public static boolean fileReference(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReference")) return false;
    if (!nextTokenIs(b, "<file reference>", CM, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILE_REFERENCE, "<file reference>");
    r = fileReference_0(b, l + 1);
    if (!r) r = fileReference_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CM fileName CM
  private static boolean fileReference_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReference_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CM);
    r = r && fileName(b, l + 1);
    r = r && consumeToken(b, CM);
    exit_section_(b, m, null, r);
    return r;
  }

  // (fileName)
  private static boolean fileReference_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReference_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fileName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // fileReference ((SPACE* CS)? SPACE* fileReference)* SPACE*
  public static boolean fileReferences(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences")) return false;
    if (!nextTokenIs(b, "<file references>", CM, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FILE_REFERENCES, "<file references>");
    r = fileReference(b, l + 1);
    r = r && fileReferences_1(b, l + 1);
    r = r && fileReferences_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // ((SPACE* CS)? SPACE* fileReference)*
  private static boolean fileReferences_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!fileReferences_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "fileReferences_1", c)) break;
    }
    return true;
  }

  // (SPACE* CS)? SPACE* fileReference
  private static boolean fileReferences_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fileReferences_1_0_0(b, l + 1);
    r = r && fileReferences_1_0_1(b, l + 1);
    r = r && fileReference(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SPACE* CS)?
  private static boolean fileReferences_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_1_0_0")) return false;
    fileReferences_1_0_0_0(b, l + 1);
    return true;
  }

  // SPACE* CS
  private static boolean fileReferences_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = fileReferences_1_0_0_0_0(b, l + 1);
    r = r && consumeToken(b, CS);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean fileReferences_1_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_1_0_0_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "fileReferences_1_0_0_0_0", c)) break;
    }
    return true;
  }

  // SPACE*
  private static boolean fileReferences_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "fileReferences_1_0_1", c)) break;
    }
    return true;
  }

  // SPACE*
  private static boolean fileReferences_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fileReferences_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "fileReferences_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // featureName (SEPARATOR featureName)*
  public static boolean lpq(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = featureName(b, l + 1);
    r = r && lpq_1(b, l + 1);
    exit_section_(b, m, LPQ, r);
    return r;
  }

  // (SEPARATOR featureName)*
  private static boolean lpq_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!lpq_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lpq_1", c)) break;
    }
    return true;
  }

  // SEPARATOR featureName
  private static boolean lpq_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpq_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SEPARATOR);
    r = r && featureName(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // lpq ((SPACE* CS)? SPACE* lpq)* SPACE*
  public static boolean lpqReferences(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lpq(b, l + 1);
    r = r && lpqReferences_1(b, l + 1);
    r = r && lpqReferences_2(b, l + 1);
    exit_section_(b, m, LPQ_REFERENCES, r);
    return r;
  }

  // ((SPACE* CS)? SPACE* lpq)*
  private static boolean lpqReferences_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!lpqReferences_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "lpqReferences_1", c)) break;
    }
    return true;
  }

  // (SPACE* CS)? SPACE* lpq
  private static boolean lpqReferences_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lpqReferences_1_0_0(b, l + 1);
    r = r && lpqReferences_1_0_1(b, l + 1);
    r = r && lpq(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (SPACE* CS)?
  private static boolean lpqReferences_1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_1_0_0")) return false;
    lpqReferences_1_0_0_0(b, l + 1);
    return true;
  }

  // SPACE* CS
  private static boolean lpqReferences_1_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_1_0_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = lpqReferences_1_0_0_0_0(b, l + 1);
    r = r && consumeToken(b, CS);
    exit_section_(b, m, null, r);
    return r;
  }

  // SPACE*
  private static boolean lpqReferences_1_0_0_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_1_0_0_0_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "lpqReferences_1_0_0_0_0", c)) break;
    }
    return true;
  }

  // SPACE*
  private static boolean lpqReferences_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "lpqReferences_1_0_1", c)) break;
    }
    return true;
  }

  // SPACE*
  private static boolean lpqReferences_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "lpqReferences_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SPACE)) break;
      if (!empty_element_parsed_guard_(b, "lpqReferences_2", c)) break;
    }
    return true;
  }

}
