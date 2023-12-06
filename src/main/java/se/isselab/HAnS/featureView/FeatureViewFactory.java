/*
Copyright 2021 Herman Jansson & Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.featureView;

import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.*;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.AnnotationIcons;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;

import static com.intellij.psi.PsiManager.getInstance;
import static com.intellij.psi.search.FilenameIndex.getAllFilesByExt;
import static com.intellij.psi.search.FilenameIndex.getVirtualFilesByName;
import static com.intellij.psi.search.GlobalSearchScope.projectScope;

public class FeatureViewFactory implements ToolWindowFactory {

    private static PsiFile featureModel;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(AnnotationIcons.FeatureModelIcon);
        var fileEditorManager = FileEditorManager.getInstance(project);
        var fileEditor = fileEditorManager.getSelectedEditor();
        var psiFile = findFeatureModel(project);
        // for testing
        featureModel = psiFile;

        JComponent component;
        if (psiFile != null)
            component = new StructureViewComponent(fileEditor, new FeatureViewModel(psiFile), project, false);
        else {
            component = getNoFeatureModelFoundPanel();
        }


        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(component, "", false);
        var contentManager = toolWindow.getContentManagerIfCreated();
        if (contentManager != null) {
            contentManager.addContent(content);
        }


    }

    @NotNull
    private JPanel getNoFeatureModelFoundPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = JBUI.insets(10);
        JLabel label = new JLabel("No feature-model could be found");
        var button = new JButton("Create feature-model", AnnotationIcons.FeatureModelIcon);
        button.addActionListener(e -> createFeatureModelFileWithDialog());

        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(label, constraints);
        constraints.gridy = 2;
        panel.add(button, constraints);
        constraints.gridy = 4;
        return panel;
    }

    private void createFeatureModelFileWithDialog() {
        // Get the project instance from the ProjectManager
        Project project = ProjectManager.getInstance().getOpenProjects()[0];

        // Get feature name from user via DialogBox
        String content = Messages.showInputDialog(project, "Root feature:", project.getBasePath(), null, "Enter_Root_Feature_Name", null);

        //Skip creating file if dialog box is cancelled
        if(content != null) {

            // wrap file creation into WriteCommandAction
            WriteCommandAction.runWriteCommandAction(project, () -> {
                // Get the base directory of the project
                String basePath = project.getBasePath();
                if (basePath != null) {
                    try {
                        // Create a new file in the base directory
                        VirtualFile featureModelFile = Objects.requireNonNull(VfsUtil.createDirectoryIfMissing(basePath))
                                .createChildData(this, ".feature-model");

                        //Get name of feature to fill feature model file
                        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                        featureModelFile.setBinaryContent(contentBytes);

                        //Remove current content from the ToolWindow and replace it with the normal feature view model
                        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Feature Model View");
                        ContentManager contentManager = Objects.requireNonNull(toolWindow).getContentManager();
                        contentManager.removeAllContents(true);
                        createToolWindowContent(project, toolWindow);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private PsiFile findFeatureModel(@NotNull Project project) {
        var allFilenames = getVirtualFilesByName(".feature-model", projectScope(project));
        PsiFile psiFile = null;
        if (allFilenames.size() > 0) {
            psiFile = getInstance(project).findFile(allFilenames.iterator().next());
        } else {
            Collection<VirtualFile> virtualFileCollection = getAllFilesByExt(project, "feature-model");
            if (!virtualFileCollection.isEmpty()) {
                psiFile = getInstance(project).findFile(virtualFileCollection.iterator().next());
            }
        }

        return psiFile;
    }

    public static PsiFile getFeatureModel() {
        return featureModel;
    }
}