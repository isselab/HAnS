package se.isselab.HAnS.folderAnnotationTests;

import com.intellij.testFramework.ParsingTestCase;
import se.isselab.HAnS.featureAnnotation.folderAnnotation.FolderAnnotationParserDefinition;

public class FolderAnnotationParsingFeatureToFolderTestCase extends ParsingTestCase {
    public FolderAnnotationParsingFeatureToFolderTestCase() {
        super("", "feature-to-folder", new FolderAnnotationParserDefinition());
    }

    public void testParsingTestData() {
        doTest(true);
    }

    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/folderAnnotationTestData/parsingTest/feature-to-folder";
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
