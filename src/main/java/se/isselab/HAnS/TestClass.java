package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.pluginExtensions.MetricsService;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback;

import java.util.Map;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MetricsService featureService = e.getProject().getService(MetricsService.class);
        var temp = featureService.getFeatures();
        featureService.getAllFeatureFileMappingsBackground(new FeatureFileMappingCallback() {
            @Override
            public void onComplete(Map<String, FeatureFileMapping> featureFileMappings) {
                printStuff(featureFileMappings);
            }
        });
        System.out.println(temp);

    }

    private void printStuff(Map<String, FeatureFileMapping> t ){
        System.out.println(t);
    }
}
