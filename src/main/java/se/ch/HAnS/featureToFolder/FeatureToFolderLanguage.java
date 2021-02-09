package se.ch.HAnS.featureToFolder;

import com.intellij.lang.Language;

public class FeatureToFolderLanguage extends Language {

    public static final FeatureToFolderLanguage INSTANCE = new FeatureToFolderLanguage();

    private FeatureToFolderLanguage() { super("FeatureToFolder"); }
}
