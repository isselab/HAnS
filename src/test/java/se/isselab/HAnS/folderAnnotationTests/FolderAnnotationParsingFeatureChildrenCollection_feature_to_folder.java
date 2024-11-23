package se.isselab.HAnS.folderAnnotationTests;

import com.intellij.testFramework.ParsingTestCase;
import se.isselab.HAnS.folderAnnotation.FolderAnnotationParserDefinition;

public class FolderAnnotationParsingFeatureChildrenCollection_feature_to_folder extends ParsingTestCase {
    public FolderAnnotationParsingFeatureChildrenCollection_feature_to_folder() {
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
