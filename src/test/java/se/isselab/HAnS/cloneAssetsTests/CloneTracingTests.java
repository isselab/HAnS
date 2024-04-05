package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.cloneManagement.FeatureModelHandler;
import se.isselab.HAnS.assetsManagement.cloneManagement.FeaturesAnnotationsExtractor;
import se.isselab.HAnS.assetsManagement.cloneManagement.TracingHandler;

import java.util.regex.Pattern;

public class CloneTracingTests extends BasePlatformTestCase {
    TracingHandler tracingHandler;
    FeatureModelHandler featureModelHandler;
    FeaturesAnnotationsExtractor featuresAnnotationsExtractor;


    @Before
    public void setUp() {
        tracingHandler = new TracingHandler();
        featureModelHandler = new FeatureModelHandler();
        featuresAnnotationsExtractor = new FeaturesAnnotationsExtractor();
    }

    @Test
    public void testStoreCloneTrace() throws Exception {
    }

    @Test
    public void testCreateCopyFeatureTrace() {
    }

    @Test
    public void testFindFeatureToFolderMappings() {
    }

    @Test
    public void testGetCurrentDateAndTime() {
        String dateTime = tracingHandler.getCurrentDateAndTime();
        String regex = "\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        assertTrue("The date and time format should match yyyyMMddHHmmss",pattern.matcher(dateTime).matches());
    }
}
