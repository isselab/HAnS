package se.ch.HAnS.folderAnnotations;

import com.intellij.lang.Language;

public class FolderAnnotationLanguage extends Language {

    public static final FolderAnnotationLanguage INSTANCE = new FolderAnnotationLanguage();

    private FolderAnnotationLanguage() { super("FolderAnnotation"); }
}
