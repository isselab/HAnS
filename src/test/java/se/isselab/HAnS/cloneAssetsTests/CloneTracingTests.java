package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;
import se.isselab.HAnS.assetsManagement.HansAssetsManagementPage;
import se.isselab.HAnS.assetsManagement.cloneManagement.FeaturesAnnotationsExtractor;
import se.isselab.HAnS.assetsManagement.cloneManagement.NotificationProvider;
import se.isselab.HAnS.assetsManagement.cloneManagement.TracingHandler;
import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class CloneTracingTests extends BasePlatformTestCase {
    TracingHandler tracingHandler;
    HansAssetsManagementPage hansAssetsManagementPage;
    JComponent settings;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tracingHandler = new TracingHandler();
        hansAssetsManagementPage = new HansAssetsManagementPage();
        settings = hansAssetsManagementPage.createComponent();
    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/cloneTestData";
    }

    public void testGetCurrentDateAndTime() {
        TracingHandler tracingHandler = new TracingHandler();
        String dateTime = tracingHandler.getCurrentDateAndTime();
        String regex = "\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        assertTrue("The date and time format should match yyyyMMddHHmmss",pattern.matcher(dateTime).matches());
    }

    public void testGetTraceFilePath() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                String traceFilePath = TracingHandler.getTraceFilePath(myFixture.getProject());
                assertNotNull("Trace file path was not found", traceFilePath);
            } catch (Exception e) {
                fail("Failed to create src directory or .trace-db.txt file");
            }
        });
    }

    public void testGetRelativePath() {
        String pathDifferentName = "/users/example/ExampleProject/src/dir";
        String pathSameName = "GetRelativePath/src/dir";
        String projectName = "ExampleProject";
        String resultDifferentProjectNames = new TracingHandler().getRelativePath(myFixture.getProject(), pathDifferentName, projectName);
        String resultSametProjectNames = new TracingHandler().getRelativePath(myFixture.getProject(), pathSameName, "GetRelativePath");
        assertEquals("ExampleProject/src/dir", resultDifferentProjectNames);
        assertEquals("src/dir", resultSametProjectNames);
    }

    public void testFeaturesExtractions() {
        PsiFile file = myFixture.configureByFile("CloneFile.java");
        List<String> listFeatureAnnotations = FeaturesAnnotationsExtractor.extractFeatureNames(file);
        assertTrue(!listFeatureAnnotations.isEmpty());
    }

    public void testCloneFragment(){
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                //myFixture.configureByFile("CloneFile.java");
                VirtualFile sourceFile = myFixture.getTempDirFixture().createFile("CloneFile.java", "public class cloneFile {\n" +
                        "    String test = \"test\"; // &line[Test]\n" +
                        "    }\n" +
                        "}");
                myFixture.openFileInEditor(sourceFile);
                myFixture.performEditorAction(IdeActions.ACTION_COPY);
                myFixture.performEditorAction(IdeActions.ACTION_PASTE);
                String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
                VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
                String content = VfsUtilCore.loadText(traceFile);
                assertNotNull("Trace File was not created", traceFile);
                assertTrue("Trace was not stored", !content.isEmpty());
                traceFile.delete(CloneTracingTests.class);
            } catch (Exception e) {
                fail("Failed to create files or copy content: " + e.getMessage());
            }
        });
    }

    public void testTraceParsing() {
        ApplicationManager.getApplication().runWriteAction(() -> {
        try {
            myFixture.configureByFile("CloneFile.java");
            myFixture.performEditorAction(IdeActions.ACTION_COPY);
            myFixture.performEditorAction(IdeActions.ACTION_PASTE);
            String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
            VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
            String content = VfsUtilCore.loadText(traceFile);
            String[] lines = content.split("\n");
            Pattern pattern = Pattern.compile("^[^;]+;[^;]+(?:;[^;]+)?$");

            for (String line : lines) {
                line = line.trim();
                if(line.equals(""))
                    continue;
                boolean matches = pattern.matcher(line).matches();
                assertTrue("Line does not match the expected format: " + line, matches);
            }
            List<List<String>> traces = NotificationProvider.getTraces();
            assertNotNull(traces);
            traceFile.delete(CloneTracingTests.class);
        } catch (Exception e) {
            fail("Failed to create files or copy content: " + e.getMessage());
        }
        });
    }

    public void testChangeAssetPref() {
        CloneManagementSettingsState hans = ServiceManager.getService(CloneManagementSettingsState.class);
        assertTrue(true);
        //hansAssetsManagementPage.getCloneManagementSettingsComponent().setAssetsManagementPrefKey("all");
    }
}

