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
package se.ch.HAnS.featureView;

import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.AnnotationIcons;

import java.util.Collection;

import static com.intellij.psi.PsiManager.getInstance;
import static com.intellij.psi.search.FilenameIndex.getAllFilesByExt;
import static com.intellij.psi.search.FilenameIndex.getVirtualFilesByName;
import static com.intellij.psi.search.GlobalSearchScope.projectScope;

public class FeatureViewFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(AnnotationIcons.FeatureModelIcon);
        var fileEditor = FileEditorManager.getInstance(project).getAllEditors()[0];
        var psiFile = findFeatureModel(project);

        @NotNull StructureViewComponent tab = new StructureViewComponent(fileEditor, new FeatureViewModel(psiFile), project, false);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(tab, "", false);
        toolWindow.getContentManager().addContent(content);

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
}