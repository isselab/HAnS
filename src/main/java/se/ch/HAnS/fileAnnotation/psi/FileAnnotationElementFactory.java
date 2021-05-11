package se.ch.HAnS.fileAnnotation.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.fileAnnotation.psi.impl.FileAnnotationLpqImpl;
import se.ch.HAnS.folderAnnotation.FolderAnnotationFileType;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;

public class FileAnnotationElementFactory {

    public static FileAnnotationLpq createLPQ(Project project, String name) {
        final FileAnnotationFile file = createFile(project, name);
        return file.findChildByClass(FileAnnotationLpq.class);
    }

    public static FileAnnotationFile createFile(Project project, String text) {
        String name = "dummy.feature-to-folder";
        return (FileAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FileAnnotationFileType.INSTANCE, "dummy.file\n" + text);
    }
}