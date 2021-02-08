package featureModel;

import com.intellij.lang.Language;

public class FeatureModelLanguage extends Language {

    public static final FeatureModelLanguage INSTANCE = new FeatureModelLanguage();

    private FeatureModelLanguage() {
        super("FeatureModel");
    }

}