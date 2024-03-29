package se.isselab.HAnS.fileAnnotationTests;

import com.intellij.testFramework.ParsingTestCase;
import se.isselab.HAnS.fileAnnotation.FileAnnotationParserDefinition;

public class FileAnnotationParsingTest_feature_file extends ParsingTestCase {
    public FileAnnotationParsingTest_feature_file() {
        super("", "feature-file", new FileAnnotationParserDefinition());
    }

    public void testParsingTestData(){
        doTest(true);
    }


    /**
     * @return path to test data file directory relative to root of this module.
     */
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/fileAnnotationTestData/parsingTest/feature-file";
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

