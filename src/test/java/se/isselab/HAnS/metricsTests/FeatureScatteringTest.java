package se.isselab.HAnS.metricsTests;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;

public class FeatureScatteringTest extends BasePlatformTestCase {

    public void testScatteringDegreeContiguousAndFragmented() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // Create two contiguous lines block 0..1 and another contiguous block 3..4 -> should be 2 segments
        mapping.enqueue("/some/File.java", 0, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 1, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");

        // Add non-contiguous lines as another separated segment
        mapping.enqueue("/some/File.java", 3, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 4, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Expected contiguous grouping: lines {0,1} -> 1 segment, {3,4} -> 1 segment => total 2
        assertEquals(2, scattering);
    }
}
