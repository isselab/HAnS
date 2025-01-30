package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureModel.FoldRegionWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FoldAction extends AnAction {
    private static final Pattern BLOCK_BEGIN_PATTERN = Pattern.compile("// &begin\\[(.*?)\\]");
    private static final Pattern BLOCK_END_PATTERN = Pattern.compile("// &end\\[(.*?)\\]");

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        Project project = e.getProject();

        if (editor == null || project == null) {
            return;
        }

        Document document = editor.getDocument();
        FoldingModel foldingModel = editor.getFoldingModel();

        foldingModel.runBatchFoldingOperation(() -> {
            Matcher beginMatcher = BLOCK_BEGIN_PATTERN.matcher(document.getText());
            Matcher endMatcher = BLOCK_END_PATTERN.matcher(document.getText());

            while (beginMatcher.find()) {
                String featureName = beginMatcher.group(1);
                int startOffset = beginMatcher.end();

                while (endMatcher.find()) {
                    if (endMatcher.group(1).equals(featureName)) {
                        int endOffset = endMatcher.start();
                        FoldRegion foldRegion = foldingModel.addFoldRegion(startOffset, endOffset, "...");
                        if (foldRegion != null) {
                            foldRegion.setExpanded(true);
                        }
                        break;
                    }
                }
            }
        });
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(e.getData(CommonDataKeys.EDITOR) != null);
    }
}
