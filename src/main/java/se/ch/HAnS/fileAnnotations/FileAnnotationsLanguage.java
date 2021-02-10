package se.ch.HAnS.fileAnnotations;

import com.intellij.lang.Language;

public class FileAnnotationsLanguage extends Language {

    public static final FileAnnotationsLanguage INSTANCE = new FileAnnotationsLanguage();

    private FileAnnotationsLanguage() {
        super("FileAnnotations");
    }

}