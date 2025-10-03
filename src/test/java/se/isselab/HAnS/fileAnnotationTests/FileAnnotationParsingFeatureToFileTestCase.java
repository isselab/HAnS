package se.isselab.HAnS.fileAnnotationTests;

import com.intellij.testFramework.ParsingTestCase;
import se.isselab.HAnS.featureAnnotation.fileAnnotation.FileAnnotationParserDefinition;

public class FileAnnotationParsingFeatureToFileTestCase extends ParsingTestCase {
    public FileAnnotationParsingFeatureToFileTestCase() {
        super("", "feature-to-file", new FileAnnotationParserDefinition());
    }

    public void testParsingTestData(){
        doTest(true);
    }


    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/fileAnnotationTestData/parsingTest/feature-to-file";
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

