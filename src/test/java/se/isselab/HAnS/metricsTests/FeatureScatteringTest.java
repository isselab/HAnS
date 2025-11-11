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

    public void testScatteringDegreeSingleContiguousBlock() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // Create a single contiguous block
        mapping.enqueue("/some/File.java", 10, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 20, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Single contiguous block -> 1 segment
        assertEquals(1, scattering);
    }

    public void testScatteringDegreeMultipleFiles() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // File 1: 2 segments
        mapping.enqueue("/src/File1.java", 0, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/src/File1.java", 5, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");

        // File 2: 1 segment
        mapping.enqueue("/src/File2.java", 10, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/src/File2.java", 15, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // File1: 2 segments, File2: 1 segment => total 3
        assertEquals(3, scattering);
    }

    public void testScatteringDegreeHighlyFragmented() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // Create 5 separate single-line segments
        mapping.enqueue("/some/File.java", 0, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 2, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 4, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 6, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 8, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // 5 separate segments
        assertEquals(5, scattering);
    }

    public void testScatteringDegreeEmptyMapping() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // No locations added
        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // No locations -> 0 segments
        assertEquals(0, scattering);
    }

    public void testScatteringDegreeBeginEndMarkers() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // Create blocks using BEGIN/END markers - two separate blocks
        mapping.enqueue("/some/File.java", 10, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 15, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.enqueue("/some/File.java", 20, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 25, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Two separate blocks -> 2 segments
        assertEquals(2, scattering);
    }

    public void testScatteringDegreeAdjacentBlocks() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // Create adjacent blocks - should be counted as separate segments since they don't overlap
        mapping.enqueue("/some/File.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 2, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.enqueue("/some/File.java", 3, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 5, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Blocks 0-2 and 3-5 are contiguous -> 1 segment
        assertEquals(1, scattering);
    }
}
