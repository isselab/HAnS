package se.ch.HAnS.codeAnnotation.psi;

import com.google.common.collect.Iterables;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.codeAnnotation.CodeAnnotationFileType;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationFile;
import se.ch.HAnS.fileAnnotation.psi.FileAnnotationLpq;

import java.util.Collection;

public class CodeAnnotationElementFactory {

    public static CodeAnnotationLpq createLPQ(Project project, String name) {
        final CodeAnnotationFile file = createFile(project, name);
        @NotNull Collection<CodeAnnotationLpq> lpq = PsiTreeUtil.collectElementsOfType(file, CodeAnnotationLpq.class);
        return Iterables.get(lpq, 0);
    }

    public static CodeAnnotationFile createFile(Project project, String text) {
        String name = "dummy.feature-to-folder";
        return (CodeAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, CodeAnnotationFileType.INSTANCE, "&begin[" + text + "]");
    }
}