package se.isselab.HAnS.metricsTests;

import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;

import java.util.HashMap;
import java.util.Map;

public class FeatureScatteringTest extends BasePlatformTestCase {

    public void testScatteringDegreeContiguousAndFragmented() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        // Create SmartPsiElementPointer
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        // Build marker data map
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        
        // Create two contiguous lines block 0..1 and another contiguous block 3..4 -> should be 2 segments
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/some/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 0);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 1);
        // Add non-contiguous lines as another separated segment
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 3);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 4);

        // Create immutable FeatureFileMapping
        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Expected contiguous grouping: lines {0,1} -> 1 segment, {3,4} -> 1 segment => total 2
        assertEquals(2, scattering);
    }

    public void testScatteringDegreeSingleContiguousBlock() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        
        // Create a single contiguous block
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/some/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 20);

        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Single contiguous block -> 1 segment
        assertEquals(1, scattering);
    }

    public void testScatteringDegreeMultipleFiles() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();

        // File 1: 2 segments
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File1.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.LINE, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.LINE, 5);

        // File 2: 1 segment
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File2.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 15);

        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // File1: 2 segments, File2: 1 segment => total 3
        assertEquals(3, scattering);
    }

    public void testScatteringDegreeHighlyFragmented() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();

        // Create 5 separate single-line segments
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/some/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 0);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 2);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 4);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 6);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 8);

        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // 5 separate segments
        assertEquals(5, scattering);
    }

    public void testScatteringDegreeEmptyMapping() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        // No locations added
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // No locations -> 0 segments
        assertEquals(0, scattering);
    }

    public void testScatteringDegreeBeginEndMarkers() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();

        // Create blocks using BEGIN/END markers - two separate blocks
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/some/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 15);
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 20);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 25);

        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Two separate blocks -> 2 segments
        assertEquals(2, scattering);
    }

    public void testScatteringDegreeAdjacentBlocks() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(feature.getProject()).createSmartPsiElementPointer(feature);

        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();

        // Create adjacent blocks - should be counted as separate segments since they don't overlap
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/some/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 2);
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 3);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 5);

        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        // Blocks 0-2 and 3-5 are contiguous -> 1 segment
        assertEquals(1, scattering);
    }
}
