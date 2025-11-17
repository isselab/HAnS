package se.isselab.HAnS.metricsTests;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.NestingDepths;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestingDepthsTest extends BasePlatformTestCase {

    public void testNestingDepthMinimalCase() {
        // Feature with no nesting - depth should be 1
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 15);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap);

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> nestingDepths = depthMap.get(ReadAction.compute(f1::getLPQText));
        assertNotNull(nestingDepths);
        assertEquals(1, nestingDepths.size());
        assertEquals("/src/File.java", nestingDepths.get(0).first);
        assertEquals(1, nestingDepths.get(0).second.intValue());
    }

    public void testNestingDepthSingleLevel() {
        // F2 nested inside F1 - F2 should have depth 2
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // F1: lines 0-20
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 20);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        // F2: lines 5-10 (nested inside F1)
        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 5);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 10);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> f1Depths = depthMap.get(ReadAction.compute(f1::getLPQText));
        List<Pair<String, Integer>> f2Depths = depthMap.get(ReadAction.compute(f2::getLPQText));

        assertNotNull(f1Depths);
        assertNotNull(f2Depths);

        // F1 is not nested -> depth 1
        assertEquals(1, f1Depths.get(0).second.intValue());

        // F2 is nested inside F1 -> depth 2 (base 1 + 1 nesting level)
        assertEquals(2, f2Depths.get(0).second.intValue());
    }

    public void testNestingDepthMultiLevel() {
        // F3 nested inside F2, which is nested inside F1 - F3 should have depth 3
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n    F3\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        FeatureModelFeature f3 = features.stream().filter(f -> f.getFeatureName().equals("F3")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);
        assertNotNull(f3);

        // F1: lines 0-30
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 30);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        // F2: lines 5-25 (nested inside F1)
        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 5);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 25);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        // F3: lines 10-20 (nested inside F2, which is inside F1)
        SmartPsiElementPointer<FeatureModelFeature> f3Pointer = 
            SmartPointerManager.getInstance(f3.getProject()).createSmartPsiElementPointer(f3);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap3 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key3 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder3 = markerDataMap3.computeIfAbsent(key3, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder3.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder3.addMarker(FeatureFileMapping.MarkerType.END, 20);
        FeatureFileMapping m3 = FeatureFileMapping.create(f3Pointer, markerDataMap3);

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);
        fileMappings.put(ReadAction.compute(f3::getLPQText), m3);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> f1Depths = depthMap.get(ReadAction.compute(f1::getLPQText));
        List<Pair<String, Integer>> f2Depths = depthMap.get(ReadAction.compute(f2::getLPQText));
        List<Pair<String, Integer>> f3Depths = depthMap.get(ReadAction.compute(f3::getLPQText));

        assertNotNull(f1Depths);
        assertNotNull(f2Depths);
        assertNotNull(f3Depths);

        // F1 is not nested -> depth 1
        assertEquals(1, f1Depths.get(0).second.intValue());

        // F2 is nested inside F1 -> depth 2
        assertEquals(2, f2Depths.get(0).second.intValue());

        // F3 is nested inside F2 and F1 -> depth 3
        assertEquals(3, f3Depths.get(0).second.intValue());
    }

    public void testNestingDepthPartialOverlap() {
        // F2 partially overlaps with F1 (not fully nested)
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // F1: lines 0-15
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 15);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        // F2: lines 10-20 (partially overlaps, not fully nested)
        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 20);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> f2Depths = depthMap.get(ReadAction.compute(f2::getLPQText));

        assertNotNull(f2Depths);

        // F2 is not fully nested inside F1, so nesting depth should be 1
        assertEquals(1, f2Depths.get(0).second.intValue());
    }

    public void testNestingDepthMultipleFiles() {
        // Same feature in multiple files with different nesting depths
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // File1: F2 nested inside F1
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File1.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 20);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        
        FeatureFileMapping.FileAnnotationKey key2a = new FeatureFileMapping.FileAnnotationKey("/src/File1.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2a = markerDataMap2.computeIfAbsent(key2a, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2a.addMarker(FeatureFileMapping.MarkerType.BEGIN, 5);
        builder2a.addMarker(FeatureFileMapping.MarkerType.END, 10);

        // File2: F2 not nested
        FeatureFileMapping.FileAnnotationKey key2b = new FeatureFileMapping.FileAnnotationKey("/src/File2.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2b = markerDataMap2.computeIfAbsent(key2b, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2b.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder2b.addMarker(FeatureFileMapping.MarkerType.END, 5);
        
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> f2Depths = depthMap.get(ReadAction.compute(f2::getLPQText));

        assertNotNull(f2Depths);
        assertEquals(2, f2Depths.size());

        // Find depths for each file
        Pair<String, Integer> file1Depth = f2Depths.stream().filter(p -> p.first.equals("/src/File1.java")).findFirst().orElse(null);
        Pair<String, Integer> file2Depth = f2Depths.stream().filter(p -> p.first.equals("/src/File2.java")).findFirst().orElse(null);

        assertNotNull(file1Depth);
        assertNotNull(file2Depth);

        // File1: F2 nested inside F1 -> depth 2
        assertEquals(2, file1Depth.second.intValue());

        // File2: F2 not nested -> depth 1
        assertEquals(1, file2Depth.second.intValue());
    }

    public void testNestingDepthWithMultipleBlocks() {
        // Feature with multiple blocks, some nested at different levels
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // F1: two separate blocks
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 10);
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 20);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 30);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        // F2: one block nested in first F1 block, another nested in second F1 block
        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 3);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 5);
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 23);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 25);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> f2Depths = depthMap.get(ReadAction.compute(f2::getLPQText));

        assertNotNull(f2Depths);

        // F2 blocks are nested inside F1 blocks -> depth 2
        // The nesting depth calculation counts nestings per file, so we get one entry per file
        assertEquals(1, f2Depths.size());
        assertEquals(2, f2Depths.get(0).second.intValue());
    }

    public void testNestingDepthNoFeatureLocations() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap); // No locations

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);

        Map<String, List<Pair<String, Integer>>> depthMap = NestingDepths.getNestingDepthMap(fileMappings);
        List<Pair<String, Integer>> f1Depths = depthMap.get(ReadAction.compute(f1::getLPQText));

        // Feature with no locations should return null or empty
        assertTrue(f1Depths == null || f1Depths.isEmpty());
    }
}
