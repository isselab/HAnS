package se.ch.HAnS.folderAnnotation.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import se.ch.HAnS.folderAnnotation.FolderAnnotationFileType;

public class FolderAnnotationElementFactory {

    public static FolderAnnotationLpq createLPQ(Project project, String name) {
        final FolderAnnotationFile file = createFile(project, name);
        return (FolderAnnotationLpq) file.getFirstChild();
    }

    public static FolderAnnotationFile createFile(Project project, String text) {
        String name = "dummy.feature-to-folder";
        return (FolderAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FolderAnnotationFileType.INSTANCE, text);
    }
}