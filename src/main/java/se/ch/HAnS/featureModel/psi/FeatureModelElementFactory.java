package se.ch.HAnS.featureModel.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import se.ch.HAnS.featureModel.FeatureModelFileType;

public class FeatureModelElementFactory {

    public static FeatureModelFeature createFeature(Project project, String name) {
        final FeatureModelFile file = createFile(project, name);
        return (FeatureModelFeature) file.getFirstChild();
    }

    public static FeatureModelFile createFile(Project project, String text) {
        String name = "dummy.feature-model";
        return (FeatureModelFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, FeatureModelFileType.INSTANCE, text);
    }
}