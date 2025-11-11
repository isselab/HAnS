package se.isselab.HAnS.featureLocationTests;

import com.intellij.openapi.util.Pair;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.ArrayList;
import java.util.List;

public class FeatureLocationBlockTest extends BasePlatformTestCase {

    private FeatureLocationBlock createBlock(int start, int end) {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    TestFeature");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);
        mapping.enqueue("/test/File.java", start, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "origin");
        mapping.enqueue("/test/File.java", end, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "origin");
        mapping.buildFromQueue();
        
        Pair<String, String> key = new Pair<>("/test/File.java", "origin");
        se.isselab.HAnS.featureLocation.FeatureLocation location = mapping.getFeatureLocationsForFile(key);
        assertNotNull(location);
        List<FeatureLocationBlock> blocks = location.getFeatureLocations();
        assertNotNull(blocks);
        assertFalse(blocks.isEmpty());
        return blocks.get(0);
    }

    public void testLineCountAndToString() {
        FeatureLocationBlock block = createBlock(2, 4);
        assertEquals(3, block.getLineCount());
        assertTrue(block.toString().contains("Start:"));
    }

    public void testHasSharedLinesAndArrayVariant() {
        FeatureLocationBlock a = createBlock(0, 2);
        FeatureLocationBlock b = createBlock(3, 5);
        FeatureLocationBlock c = createBlock(2, 4);

        assertFalse(a.hasSharedLines(b));
        assertTrue(a.hasSharedLines(c));

        FeatureLocationBlock[] arr = new FeatureLocationBlock[]{b, c};
        assertTrue(a.hasSharedLines(arr));
    }

    public void testIsInsideOfBlockAndCountTimesInside() {
        FeatureLocationBlock outer = createBlock(0, 10);
        FeatureLocationBlock inner1 = createBlock(2, 3);
        FeatureLocationBlock inner2 = createBlock(4, 6);

        assertTrue(inner1.isInsideOfBlock(outer));
        assertTrue(inner2.isInsideOfBlock(outer));

        List<FeatureLocationBlock> blocks = new ArrayList<>();
        blocks.add(outer);
        blocks.add(createBlock(0, 2));

        // inner1 is inside of outer only
        assertEquals(1, inner1.countTimesInsideOfBlocks(blocks));
    }
}
