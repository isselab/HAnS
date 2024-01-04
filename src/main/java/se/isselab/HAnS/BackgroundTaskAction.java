package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureExtension.BackgroundTask;
import se.isselab.HAnS.featureExtension.FeatureService;

public class BackgroundTaskAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FeatureService featureService = new FeatureService();
        System.out.println("starting background");
        //BackgroundTask task = new BackgroundTask(e.getProject(), "Scanning features", new CallbackClass());

        // ProgressManager.getInstance().runProcessWithProgressAsynchronously(task, new EmptyProgressIndicator());
    }
}
