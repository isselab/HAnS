package se.isselab.HAnS.metricsTests;

import com.intellij.openapi.application.ReadAction;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;

import java.util.HashMap;
import java.util.List;
import java.util.HashSet;

public class FeatureTanglingTest extends BasePlatformTestCase {

    public void testTangledFeaturesDetected() {
        // Create a feature model with two features
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        FeatureFileMapping m2 = new FeatureFileMapping(f2);

        // both annotate overlapping lines in same file
        m1.enqueue("/src/FileX.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/FileX.java", 2, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m2.enqueue("/src/FileX.java", 1, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/FileX.java", 3, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m1.buildFromQueue();
        m2.buildFromQueue();

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

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        FeatureFileMapping m2 = new FeatureFileMapping(f2);

        // Non-overlapping blocks
        m1.enqueue("/src/FileX.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/FileX.java", 2, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m2.enqueue("/src/FileX.java", 5, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/FileX.java", 7, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m1.buildFromQueue();
        m2.buildFromQueue();

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

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        FeatureFileMapping m2 = new FeatureFileMapping(f2);
        FeatureFileMapping m3 = new FeatureFileMapping(f3);

        // F1 overlaps with both F2 and F3
        m1.enqueue("/src/FileX.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/FileX.java", 10, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m2.enqueue("/src/FileX.java", 2, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/FileX.java", 5, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m3.enqueue("/src/FileX.java", 7, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m3.enqueue("/src/FileX.java", 9, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m1.buildFromQueue();
        m2.buildFromQueue();
        m3.buildFromQueue();

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

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        FeatureFileMapping m2 = new FeatureFileMapping(f2);

        // F1 and F2 overlap in multiple places - TD should still be 1 (unique count)
        m1.enqueue("/src/FileX.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/FileX.java", 5, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m1.enqueue("/src/FileX.java", 10, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/FileX.java", 15, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m2.enqueue("/src/FileX.java", 2, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/FileX.java", 4, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m2.enqueue("/src/FileX.java", 12, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/FileX.java", 14, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");

        m1.buildFromQueue();
        m2.buildFromQueue();

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

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.buildFromQueue(); // No locations

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

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.enqueue("/src/FileX.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/FileX.java", 5, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.buildFromQueue();

        HashMap<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);

        int td = FeatureTangling.getFeatureTanglingDegree(getProject(), fileMappings, f1);

        // Feature present but no tangling with others -> TD = 0
        assertEquals(0, td);
    }
}
