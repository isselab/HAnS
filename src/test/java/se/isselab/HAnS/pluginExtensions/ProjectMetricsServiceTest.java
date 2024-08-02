package se.isselab.HAnS.pluginExtensions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.pluginExtensions.backgroundTasks.featureFileMappingTasks.FeatureFileMappingCallback;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ProjectMetricsServiceTest extends BasePlatformTestCase {

    @BeforeEach
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        /*myFixture = IdeaTestFixtureFactory.getFixtureFactory()
                .createCodeInsightFixture(IdeaTestFixtureFactory.getFixtureFactory()
                        .createFixtureBuilder(getTestName(true)).getFixture());*/



        ApplicationManager.getApplication().invokeAndWait(() -> {
            //myFixture.copyDirectoryToProject("datasetbitcoinwallet", "");
            myFixture.copyDirectoryToProject("datasetbitcoinwallet", "");

        });
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/resources/testData";
    }

    @Test
    @DisplayName("Test getFeatures method")
    public void testGetFeatures() throws Exception {
        CompletableFuture<Map<String, FeatureFileMapping>> completableFuture = new CompletableFuture<>();

            MetricsService metricsService = myFixture.getProject().getService(MetricsService.class);
        AtomicReference< HashMap<String, FeatureFileMapping>> results = new AtomicReference<>();
            //List<FeatureModelFeature> features = metricsService.getFeatures();

        ProgressManager.getInstance().runProcess(() -> {
            results.getAndSet(ApplicationManager.getApplication().runReadAction((Computable<HashMap<String, FeatureFileMapping>>) () -> {
                var features2 = metricsService.getFeatures();
                HashMap<String, FeatureFileMapping> mappings = new HashMap<>();
                for(var feature : features2) {
                    mappings.put(feature.getFeatureName(), FeatureLocationManager.getFeatureFileMapping(myFixture.getProject(), feature));
                    mappings.put(feature.getName(), new FeatureFileMapping(feature));
                }
                return FeatureLocationManager.getAllFeatureFileMappings(myFixture.getProject());
            }));
        }, new EmptyProgressIndicator());

        System.out.println(results.get());
        //System.out.println(features);
/*        System.out.println(mappings.size());
            System.out.println(features.size());
            for (FeatureModelFeature feature : features) {
                System.out.println(feature.getName());
            }
        ;*/
    }

    private void printProjectStructure(VirtualFile directory, String indent) {
        if (directory.isDirectory()) {
            for (VirtualFile file : directory.getChildren()) {
                System.out.println(indent + (file.isDirectory() ? "+" : "-") + " " + file.getName());
                if (file.isDirectory()) {
                    printProjectStructure(file, indent + "  ");
                }
            }
        }
    }

}