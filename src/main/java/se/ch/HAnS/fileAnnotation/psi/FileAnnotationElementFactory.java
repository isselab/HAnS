package se.ch.HAnS.fileAnnotation.psi;

import com.google.common.collect.Iterables;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelFile;
import se.ch.HAnS.featureModel.psi.FeatureModelTypes;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.fileAnnotation.psi.impl.FileAnnotationLpqImpl;
import se.ch.HAnS.folderAnnotation.FolderAnnotationFileType;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationFile;
import se.ch.HAnS.folderAnnotation.psi.FolderAnnotationLpq;

import java.util.Collection;

public class FileAnnotationElementFactory {

    public static FileAnnotationLpq createLPQ(Project project, String name) {
        final FileAnnotationFile file = createLPQFile(project, name);
        @NotNull Collection<FileAnnotationLpq> lpq = PsiTreeUtil.collectElementsOfType(file, FileAnnotationLpq.class);
        return Iterables.get(lpq, 0);
    }

    public static FileAnnotationFile createLPQFile(Project project, String text) {
        String name = "dummy.feature-to-file";
        return (FileAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FileAnnotationFileType.INSTANCE, "dummy.file\n" + text);
    }

    public static FileAnnotationFileReference createFileReference(Project project, String name) {
        final FileAnnotationFile file = createFileReferenceFile(project, name);
        return file.findChildByClass(FileAnnotationFileReference.class);
    }

    public static FileAnnotationFile createFileReferenceFile(Project project, String text) {
        String name = "dummy.feature-to-file";
        return (FileAnnotationFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FileAnnotationFileType.INSTANCE, text + "\ndummy");
    }
}