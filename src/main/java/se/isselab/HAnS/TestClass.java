package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;

import org.jetbrains.annotations.NotNull;

import se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask;
import se.isselab.HAnS.singleton.HAnSManager;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        HAnSManager singleton = HAnSManager.getInstance();
        startBackgroundTask(singleton);
    }

    /**
     * Starts Background Task for FeatureLocationManager.
     *
     * @param singleton
     * @see se.isselab.HAnS.featureLocation.FeatureLocationBackgroundTask
     */
    private void startBackgroundTask(HAnSManager singleton) {
        FeatureLocationBackgroundTask backgroundTask = new FeatureLocationBackgroundTask(singleton.getProject(),
                "Scanning progress",
                true,
                PerformInBackgroundOption.DEAF);
        // TODO THESIS: singleton of ProgressIndicator
        ProgressIndicator empty = new EmptyProgressIndicator();

        ProgressManager progressManager = ProgressManager.getInstance();
        Logger.print("start background task");
        progressManager.runProcessWithProgressAsynchronously(backgroundTask, empty);
    }
}
