package se.ch.HAnS.unassignedFeature;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import se.ch.HAnS.featureModel.FeatureModelFileType;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.FeatureModelFile;

public class UnassignedFeatureFactory {
    public static FeatureModelFeature createFeature(Project project, String name) {
        final FeatureModelFile file = createFile(project, name);
        return (FeatureModelFeature) file.getFirstChild();
    }

    public static FeatureModelFile createFile(Project project, String text) {
        String name = "dummy.feature-model";
        return (FeatureModelFile) PsiFileFactory.getInstance(project).createFileFromText(name, FeatureModelFileType.INSTANCE, text);
    }

    public static FeatureModelFeature createFeature(Project project, String name, String value) {
        final FeatureModelFile file = createFile(project, name + " = " + value);
        return (FeatureModelFeature) file.getFirstChild();
    }

    //adding a newline to the end of the file
    //CRLF=[\n]
    public static PsiElement createCRLF(Project project) {
        final FeatureModelFile file = createFile(project, "\n");
        return file.getFirstChild();
    }

    //INDENT=[\t]
    public static PsiElement createPlace(Project project) {
        final FeatureModelFile file = createFile(project, "\t");
        return file.getFirstChild();
    }
}
