package se.ch.HAnS.codeAnnotations;

import com.intellij.lang.Language;

public class CodeAnnotationsLanguage extends Language {
    public static final CodeAnnotationsLanguage INSTANCE = new CodeAnnotationsLanguage();

    private CodeAnnotationsLanguage() {
        super("CodeAnnotations");
    }
}
