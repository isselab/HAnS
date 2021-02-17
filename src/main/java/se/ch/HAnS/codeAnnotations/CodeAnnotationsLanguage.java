package se.ch.HAnS.codeAnnotations;

import com.intellij.lang.Language;
import se.ch.HAnS.featureModel.FeatureModelLanguage;

public class CodeAnnotationsLanguage extends Language {
    public static final CodeAnnotationsLanguage INSTANCE = new CodeAnnotationsLanguage();

    private CodeAnnotationsLanguage() {
        super("CodeAnnotations");
    }
}
