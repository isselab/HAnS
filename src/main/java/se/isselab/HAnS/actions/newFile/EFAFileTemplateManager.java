package se.isselab.HAnS.actions.newFile;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import se.isselab.HAnS.AnnotationIcons;

public class EFAFileTemplateManager implements FileTemplateGroupDescriptorFactory {

    public static final String FEATURE_MODEL_FILE = "Feature Model";
    public static final String FEATURE_TO_FOLDER_FILE = "Folder Mapping";
    public static final String FEATURE_TO_FILE_FILE = "File Mapping";


    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("EFA - HAnS", AnnotationIcons.PluginIcon);
        group.addTemplate(new FileTemplateDescriptor(FEATURE_MODEL_FILE, AnnotationIcons.FeatureModelIcon));
        group.addTemplate(new FileTemplateDescriptor(FEATURE_TO_FOLDER_FILE, AnnotationIcons.FileType));
        group.addTemplate(new FileTemplateDescriptor(FEATURE_TO_FILE_FILE, AnnotationIcons.FileType));
        return group;
    }
}
