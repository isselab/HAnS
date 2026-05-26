package se.isselab.HAnS.featureModelTests;

import com.intellij.testFramework.ParsingTestCase;
import se.isselab.HAnS.featureModel.FeatureModelParserDefinition;

public class FeatureModelParsingTest extends ParsingTestCase {
    public FeatureModelParsingTest() {
        super("", "feature-model", new FeatureModelParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    public void testPlainHierarchy() {
        doTest(true);
    }

    public void testOrGroup() {
        doTest(true);
    }

    public void testXorGroup() {
        doTest(true);
    }

    public void testOptionalFeature() {
        doTest(true);
    }

    public void testNamedXorGroup() {
        doTest(true);
    }

    public void testKeywordPrefixedFeatures() {
        doTest(true);
    }

    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/featureModelTestData/parsingTest";
    }

    @Override
    protected boolean skipSpaces() {
        return super.skipSpaces();
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}
