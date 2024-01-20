package se.isselab.HAnS.fileHighlighter;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.PsiNavigateUtil;

import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.nio.file.Path;
import java.util.List;


public class FileHighlighter {
    /**
     * Highlights a feature in the feature model of the project based on its LPQ.
     * @param project Project
     * @param featureLpq String
     */
    public static void highlightFeatureInFeatureModel(Project project, String featureLpq) {

        List<FeatureModelFeature> selectedFeatures = ReadAction.compute(() -> FeatureModelUtil.findLPQ(project, featureLpq));
        if(selectedFeatures.isEmpty()){
            return;
        }
        ApplicationManager.getApplication().invokeLater(() -> PsiNavigateUtil.navigate(selectedFeatures.get(0)));
    }
    /**
     * Highlights a feature in the feature model of the project.
     * @param project Project
     * @param feature {@link FeatureModelFeature}
     */
    public static void highlightFeatureInFeatureModel(Project project, FeatureModelFeature feature) {
        ApplicationManager.getApplication().invokeLater(() -> PsiNavigateUtil.navigate(feature));
    }

    /**
     * Opens a file of the project in the editor
     * @param project Project
     * @param path String: Absolute path of the file
     */
    public static void openFileInProject(Project project, String path){
        ApplicationManager.getApplication().invokeLater(() -> {
            String newPath = project.getBasePath() + path;
            VirtualFile open = VirtualFileManager.getInstance().findFileByNioPath(Path.of(newPath));
            if(open == null) return;
            FileEditorManager.getInstance(project).openFile(open, false);
        });
    }
    /**
     * Opens a file of the project in the editor and highlights code block
     * @param project Project
     * @param path String: Absolute path of the file
     * @param startline of the codeblock
     * @param endline of the codeblock
     */
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
