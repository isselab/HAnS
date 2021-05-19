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
import se.ch.HAnS.featureView.FeatureViewModel;

import java.util.Collection;

public class FeatureViewFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        StructureViewFactoryImpl s = new StructureViewFactoryImpl(project);
        FileEditor e = FileEditorManager.getInstance(project).getAllEditors()[0];

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

        @NotNull StructureViewComponent tab = new StructureViewComponent(e, new FeatureViewModel(f), project, true);


        CustomizationUtil.installPopupHandler(tab, "FeatureView", ActionPlaces.getActionGroupPopupPlace("FeatureView"));

        //FeatureView tab = new FeatureView(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(tab, "", false);
        toolWindow.getContentManager().addContent(content);
    }

}