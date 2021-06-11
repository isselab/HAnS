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

import com.intellij.ide.structureView.impl.StructureViewFactoryImpl;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.ide.ui.customization.CustomizationUtil;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.AnnotationIcons;
import se.ch.HAnS.featureView.FeatureViewModel;

import java.util.Collection;

public class FeatureViewFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.setIcon(AnnotationIcons.FeatureModelIcon);
        StructureViewFactoryImpl s = new StructureViewFactoryImpl(project);
        FileEditor e = FileEditorManager.getInstance(project).getAllEditors()[0];
        PsiFile f = findFeatureModel(project);

        @NotNull StructureViewComponent tab = new StructureViewComponent(e, new FeatureViewModel(f), project, true);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(tab, "", false);
        toolWindow.getContentManager().addContent(content);

    }

    private PsiFile findFeatureModel(@NotNull Project project){
        PsiFile[] allFilenames = FilenameIndex.getFilesByName(project, ".feature-model", GlobalSearchScope.projectScope(project));
        PsiFile f = null;
        if (allFilenames.length > 0) {
            f = allFilenames[0];
        }
        else {
            Collection<VirtualFile> c = FilenameIndex.getAllFilesByExt(project, "feature-model");
            if (!c.isEmpty()) {
                f = PsiManager.getInstance(project).findFile(c.iterator().next());
            }
        }

        return f;
    }

}