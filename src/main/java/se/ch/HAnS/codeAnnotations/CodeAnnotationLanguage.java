package se.ch.HAnS.codeAnnotations;

import com.intellij.lang.Language;

public class CodeAnnotationLanguage extends Language {
    public static final CodeAnnotationLanguage INSTANCE = new CodeAnnotationLanguage();

    private CodeAnnotationLanguage() {
        super("CodeAnnotations");
    }
}
