package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;
import se.isselab.HAnS.assetsManagement.cloneManagement.FeaturesAnnotationsExtractor;
import se.isselab.HAnS.assetsManagement.cloneManagement.TracingHandler;

import java.util.List;
import java.util.regex.Pattern;

public class TracingTests extends BasePlatformTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        VfsTestUtil.createFile(getProject().getBaseDir(), ".feature-model");

    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/cloneTestData";
    }

    @Test
    public void testGetCurrentDateAndTime() {
        TracingHandler tracingHandler = new TracingHandler();
        String dateTime = tracingHandler.getCurrentDateAndTime();
        String regex = "\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        assertTrue("The date and time format should match yyyyMMddHHmmss",pattern.matcher(dateTime).matches());
    }
    @Test
    public void testGetTraceFilePath() {
        String traceFilePath = TracingHandler.getTraceFilePath(myFixture.getProject());
        assertNotNull("Trace file path was not found", traceFilePath);
    }
    @Test
    public void testGetRelativePath() {
        String absolutePathDifferentProjectName = "/users/example/ExampleProject/src/dir";
        String absolutePathSameProjectName = "GetRelativePath/src/dir";
        String projectName = "ExampleProject";
        String resultDifferentProjectNames = new TracingHandler().getRelativePath(myFixture.getProject(), absolutePathDifferentProjectName, projectName);
        String resultSametProjectNames = new TracingHandler().getRelativePath(myFixture.getProject(), absolutePathSameProjectName, "GetRelativePath");
        assertEquals("ExampleProject/src/dir", resultDifferentProjectNames);
        assertEquals("src/dir", resultSametProjectNames);
    }
    @Test
    public void testFeaturesExtractions() {
        PsiFile file = myFixture.configureByFile("CloneFile.java");
        List<String> listFeatureAnnotations = FeaturesAnnotationsExtractor.extractFeatureNames(file);
        assertTrue(!listFeatureAnnotations.isEmpty());
    }
}

