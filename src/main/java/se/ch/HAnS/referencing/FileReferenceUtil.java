package se.ch.HAnS.referencing;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.fileAnnotation.FileAnnotationFileType;
import se.ch.HAnS.folderAnnotation.FolderAnnotationFileType;

import java.util.ArrayList;
import java.util.List;

public class FileReferenceUtil {

    public static List<PsiFile> findFile(PsiElement element, String file) {
        List<PsiFile> result = new ArrayList<>();
        List<PsiFile> l = findFiles(element);
        for (PsiFile f : l) {
            if (!f.isDirectory()
                    && f.getFileType() != FileAnnotationFileType.INSTANCE
                    && f.getFileType() != FolderAnnotationFileType.INSTANCE
                    && f.getName().equals(file)) {
                result.add(f);
            }
        }
        return result;
    }

    public static List<PsiFile> findFiles(PsiElement element) {
        List<PsiFile> result = new ArrayList<>();
        PsiDirectory d = element.getContainingFile().getContainingDirectory();
        PsiFile @NotNull [] l = d.getFiles();
        for (PsiFile f : l) {
            if (!f.isDirectory()
                    && f.getFileType() != FileAnnotationFileType.INSTANCE
                    && f.getFileType() != FolderAnnotationFileType.INSTANCE) {
                result.add(f);
            }
        }

        return result;
    }

}
