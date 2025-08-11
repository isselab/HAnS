package se.isselab.HAnS.featureModelTests;

import com.intellij.testFramework.ParsingTestCase;
import se.isselab.HAnS.featureModel.FeatureModelParserDefinition;

public class FeatureModelParsingFeatureChildrenCollection extends ParsingTestCase {
    public FeatureModelParsingFeatureChildrenCollection() {
        super("", "feature-model", new FeatureModelParserDefinition());
    }

    public void testParsingTestData() {
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
