package se.isselab.HAnS.metricsTests;

import com.intellij.openapi.application.ReadAction;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;

import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

public class FeatureTanglingTest extends BasePlatformTestCase {

    public void testTangledFeaturesDetected() {
        // Create a feature model with two features
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // both annotate overlapping lines in same file
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 2);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 1);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 3);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        String lpq1 = ReadAction.compute(f1::getLPQText);
        String lpq2 = ReadAction.compute(f2::getLPQText);
        fileMappings.put(lpq1, m1);
        fileMappings.put(lpq2, m2);

        var tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);

        assertTrue(tanglingMap.containsKey(f1));
        assertTrue(tanglingMap.containsKey(f2));

        HashSet<FeatureModelFeature> tangledForF1 = tanglingMap.get(f1);
        HashSet<FeatureModelFeature> tangledForF2 = tanglingMap.get(f2);

        assertTrue(tangledForF1.contains(f2));
        assertTrue(tangledForF2.contains(f1));
    }

    public void testNoTanglingWhenNoOverlap() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // Non-overlapping blocks
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 2);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 5);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 7);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        String lpq1 = ReadAction.compute(f1::getLPQText);
        String lpq2 = ReadAction.compute(f2::getLPQText);
        fileMappings.put(lpq1, m1);
        fileMappings.put(lpq2, m2);

        var tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);

        // F1 and F2 should not be tangled since they don't overlap
        HashSet<FeatureModelFeature> tangledForF1 = tanglingMap.getOrDefault(f1, new HashSet<>());
        HashSet<FeatureModelFeature> tangledForF2 = tanglingMap.getOrDefault(f2, new HashSet<>());

        assertFalse(tangledForF1.contains(f2));
        assertFalse(tangledForF2.contains(f1));
    }

    public void testTanglingDegreeMultipleFeatures() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n    F3\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        FeatureModelFeature f3 = features.stream().filter(f -> f.getFeatureName().equals("F3")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);
        assertNotNull(f3);

        // F1 overlaps with both F2 and F3
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 10);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 2);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 5);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        SmartPsiElementPointer<FeatureModelFeature> f3Pointer = 
            SmartPointerManager.getInstance(f3.getProject()).createSmartPsiElementPointer(f3);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap3 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key3 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder3 = markerDataMap3.computeIfAbsent(key3, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder3.addMarker(FeatureFileMapping.MarkerType.BEGIN, 7);
        builder3.addMarker(FeatureFileMapping.MarkerType.END, 9);
        FeatureFileMapping m3 = FeatureFileMapping.create(f3Pointer, markerDataMap3);

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);
        fileMappings.put(ReadAction.compute(f3::getLPQText), m3);

        int td1 = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f1);
        int td2 = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f2);
        int td3 = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f3);

        // F1 is tangled with F2 and F3 -> TD = 2
        assertEquals(2, td1);
        // F2 is tangled with F1 only -> TD = 1
        assertEquals(1, td2);
        // F3 is tangled with F1 only -> TD = 1
        assertEquals(1, td3);
    }

    public void testTanglingDegreeUniqueCountPerFeature() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // F1 and F2 overlap in multiple places - TD should still be 1 (unique count)
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 5);
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 15);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 2);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 4);
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 12);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 14);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        int td1 = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f1);
        int td2 = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f2);

        // Both features tangle with each other only once (unique count) despite multiple overlaps
        assertEquals(1, td1);
        assertEquals(1, td2);
    }

    public void testTanglingDegreeZeroWhenNoLocations() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap); // No locations

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);

        int td = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f1);

        // No locations -> TD = 0
        assertEquals(0, td);
    }

    public void testTanglingDegreeZeroWhenPresentButNoTangling() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/src/FileX.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 5);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap);

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);

        int td = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f1);

        // Feature present but no tangling with others -> TD = 0
        assertEquals(0, td);
    }
}
