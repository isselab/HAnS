package se.ch.HAnS.codeAnnotation.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import se.ch.HAnS.codeAnnotation.CodeAnnotationFileType;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationLpq;

public class CodeAnnotationElementFactory {

    public static CodeAnnotationLpq createLPQ(Project project, String name) {
        final CodeAnnotationFile file = createFile(project, name);
        return file.findChildByClass(CodeAnnotationLpq.class);
    }

    public static CodeAnnotationFile createFile(Project project, String text) {
        String name = "dummy.feature-to-folder";
        return (CodeAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, CodeAnnotationFileType.INSTANCE, "&begin[" + text + "]");
    }
}