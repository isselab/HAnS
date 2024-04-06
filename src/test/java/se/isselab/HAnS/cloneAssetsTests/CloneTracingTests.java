package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.assetsManagement.cloneManagement.TracingHandler;

import java.util.regex.Pattern;

public class CloneTracingTests extends BasePlatformTestCase {
    TracingHandler tracingHandler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tracingHandler = new TracingHandler();
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
    public void testGetTraceFilePath(){
        String traceFile = TracingHandler.getTraceFilePath(myFixture.getProject());
        assertNotNull("Trace file not found", traceFile);
    }
    public void testStoreCloneTrace(){
        VirtualFile fileToCopy = myFixture.configureByFile("CloneFile.java").getVirtualFile();
        tracingHandler.storeCloneTrace(myFixture.getProject(), myFixture.getProject().getName(), fileToCopy.getPath(), fileToCopy.getPath());
        //VirtualFile traceFile = myFixture.getProject().getBaseDir().findChild(".trace-db");
        //assertNotNull("Trace file not found", traceFile);
    }

}
