package se.ch.HAnS.fileAnnotation;

import com.intellij.lang.Language;

public class FileAnnotationLanguage extends Language {

    public static final FileAnnotationLanguage INSTANCE = new FileAnnotationLanguage();

    private FileAnnotationLanguage() {
        super("FileAnnotation");
    }

}