package se.isselab.HAnS.actions.newFile;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.AnnotationIcons;

import java.util.Properties;

public class CreateNewFileAction extends CreateFileFromTemplateAction implements DumbAware {
    public CreateNewFileAction() {
        super("Feature Mapping", "Create new feature mappings", AnnotationIcons.FileType);
    }

    @Override
    protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, @NotNull CreateFileFromTemplateDialog.Builder builder) {
        builder.setTitle("New Embedded Feature Annotation File")
                .setDefaultText("_")
                .addKind("Folder mapping", AnnotationIcons.FileType, EFAFileTemplateManager.FEATURE_TO_FOLDER_FILE)
                .addKind("File mapping", AnnotationIcons.FileType, EFAFileTemplateManager.FEATURE_TO_FILE_FILE)
                .addKind("Model", AnnotationIcons.FeatureModelIcon, EFAFileTemplateManager.FEATURE_MODEL_FILE);
    }

    @Override
    protected String getActionName(PsiDirectory directory, @NotNull String newName, String templateName) {
        return "Create" + newName;
    }

    @Override
    protected PsiFile createFileFromTemplate(String name, FileTemplate template, PsiDirectory dir) {
        Properties properties = new Properties();
        properties.setProperty("Project_Name", dir.getProject().getName());
        
        try {
            return (PsiFile) FileTemplateUtil.createFromTemplate(template, name, properties, dir);
        } catch (Exception e) {
            return super.createFileFromTemplate(name, template, dir);
        }
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CreateNewFileAction;
    }
}
