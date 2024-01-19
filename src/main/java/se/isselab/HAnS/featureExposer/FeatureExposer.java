package se.isselab.HAnS.featureExposer;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.ui.JBUI;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class FeatureExposer {
    public static void highlightFeatureInFeatureModel(Project project, String featureLpq) {

        List<FeatureModelFeature> selectedFeatures = ReadAction.compute(() -> FeatureModelUtil.findLPQ(project, featureLpq));
        if(selectedFeatures.isEmpty())
            return;
        ApplicationManager.getApplication().invokeLater(() -> NavigationUtil.openFileWithPsiElement(selectedFeatures.get(0), false, false));
    }
    public static void openFileInProject(Project project, String path){
        ApplicationManager.getApplication().invokeLater(() -> {
            String newPath = project.getBasePath() + path;
            VirtualFile open = VirtualFileManager.getInstance().findFileByNioPath(Path.of(newPath));
            if(open == null) return;
            FileEditorManager.getInstance(project).openFile(open, false);
        });
    }
    public static void openFileInProject(Project project, String path, int startline, int endline){
        ApplicationManager.getApplication().invokeLater(() -> {
            String newPath = project.getBasePath() + path;
            VirtualFile open = VirtualFileManager.getInstance().findFileByNioPath(Path.of(newPath));
            if(open == null) return;
            FileEditorManager.getInstance(project).openFile(open, false);
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if(editor == null) return;
            int startOffset = editor.getDocument().getLineStartOffset(startline);
            int endOffset = editor.getDocument().getLineEndOffset(endline);

            for (RangeHighlighter highlighter : editor.getMarkupModel().getAllHighlighters()) {
                highlighter.dispose();
            }
            editor.getSelectionModel().setSelection(startOffset,endOffset);
            editor.getCaretModel().moveToOffset(startOffset);
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
        });
    }

}
