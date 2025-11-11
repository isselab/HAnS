package se.isselab.HAnS.featureLocationTests;

import com.intellij.openapi.util.Pair;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;

public class FeatureFileMappingTest extends BasePlatformTestCase {

    public void testEnqueueBuildAndLineCounts() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // add a single-line code annotation (LINE)
        mapping.enqueue("/path/FileA.java", 10, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "originA");

        // add a begin/end block
        mapping.enqueue("/path/FileA.java", 2, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "originA");
        mapping.enqueue("/path/FileA.java", 4, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "originA");

        // add a file-annotation (NONE) where endline is used
        mapping.enqueue("/path/FileB.java", 20, FeatureFileMapping.MarkerType.NONE, FeatureFileMapping.AnnotationType.FILE, "originB");

        mapping.buildFromQueue();

        Pair<String, String> pairA = new Pair<>((String) "/path/FileA.java", "originA");
        Pair<String, String> pairB = new Pair<>((String) "/path/FileB.java", "originB");

        // FileA: lines covered -> 2..4 and 10 => lines {2,3,4,10} -> 4
        assertEquals(4, mapping.getFeatureLineCountInFile(pairA));

        // FileB: NONE created block from 0..20 -> linecount = 21
        assertEquals(21, mapping.getFeatureLineCountInFile(pairB));

        // total should be sum
        int expectedTotal = mapping.getFeatureLineCountInFile(pairA) + mapping.getFeatureLineCountInFile(pairB);
        assertEquals(expectedTotal, mapping.getTotalFeatureLineCount());

        // mapped file paths should include both files
        assertTrue(mapping.getMappedFilePaths().contains("/path/FileA.java"));
        assertTrue(mapping.getMappedFilePaths().contains("/path/FileB.java"));
    }
}
