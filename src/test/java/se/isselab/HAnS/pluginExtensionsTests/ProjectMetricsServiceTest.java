package se.isselab.HAnS.pluginExtensionsTests;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Pair;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.ProjectMetrics;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;
import se.isselab.HAnS.metrics.calculators.FeatureTangling;
import se.isselab.HAnS.metrics.calculators.NestingDepths;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ProjectMetricsServiceTest extends BasePlatformTestCase {

    public void testFeatureScatteringCallsCalculator() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        FeatureFileMapping mapping = new FeatureFileMapping(f1);

        // Create scattered blocks
        mapping.enqueue("/some/File.java", 0, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 5, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        
        // Expected: two separate segments
        assertEquals(2, scattering);
    }

    public void testProjectMetricsWithSingleFeature() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.enqueue("/src/File.java", 10, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/File.java", 20, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.buildFromQueue();

        // Populate metrics on features (mimics what background tasks do in production)
        f1.setScatteringDegree(FeatureScattering.getScatteringDegree(m1));
        f1.setLineCount(m1.getTotalFeatureLineCount());

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);

        Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);
        Map<String, List<Pair<String, Integer>>> nestingDepthMap = NestingDepths.getNestingDepthMap(fileMappings);

        ProjectMetrics metrics = new ProjectMetrics(fileMappings, tanglingMap, nestingDepthMap);

        assertEquals(1, metrics.getNumberOfFeatures());
        assertEquals(1, metrics.getNumberOfAnnotatedFiles());
        assertEquals(1.0, metrics.getAvgScatteringDegree(), 0.01);
        assertEquals(11.0, metrics.getAvgLinesOfFeatureCode(), 0.01); // Lines 10-20 inclusive
        assertEquals(1.0, metrics.getAvgNestingDepth(), 0.01);
    }

    public void testProjectMetricsWithMultipleFeatures() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n    F3\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        FeatureModelFeature f3 = features.stream().filter(f -> f.getFeatureName().equals("F3")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);
        assertNotNull(f3);

        // F1: 100 lines in File1.java (scattering = 1)
        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.enqueue("/src/File1.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/File1.java", 99, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.buildFromQueue();

        // F2: scattered across 3 files (scattering = 3)
        FeatureFileMapping m2 = new FeatureFileMapping(f2);
        m2.enqueue("/src/File1.java", 10, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/File2.java", 20, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/File3.java", 30, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.buildFromQueue();

        // F3: two segments in File2.java (scattering = 2)
        FeatureFileMapping m3 = new FeatureFileMapping(f3);
        m3.enqueue("/src/File2.java", 10, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m3.enqueue("/src/File2.java", 50, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m3.buildFromQueue();

        // Populate metrics on features (mimics what background tasks do in production)
        f1.setScatteringDegree(FeatureScattering.getScatteringDegree(m1));
        f1.setLineCount(m1.getTotalFeatureLineCount());
        f2.setScatteringDegree(FeatureScattering.getScatteringDegree(m2));
        f2.setLineCount(m2.getTotalFeatureLineCount());
        f3.setScatteringDegree(FeatureScattering.getScatteringDegree(m3));
        f3.setLineCount(m3.getTotalFeatureLineCount());

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);
        fileMappings.put(ReadAction.compute(f3::getLPQText), m3);

        Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);
        Map<String, List<Pair<String, Integer>>> nestingDepthMap = NestingDepths.getNestingDepthMap(fileMappings);

        ProjectMetrics metrics = new ProjectMetrics(fileMappings, tanglingMap, nestingDepthMap);

        assertEquals(3, metrics.getNumberOfFeatures());
        assertEquals(3, metrics.getNumberOfAnnotatedFiles()); // File1, File2, File3
        assertEquals(2.0, metrics.getAvgScatteringDegree(), 0.01); // (1 + 3 + 2) / 3 = 2.0
    }

    public void testProjectMetricsWithTangling() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        // F1 and F2 tangled in the same file
        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.enqueue("/src/File.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/File.java", 10, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.buildFromQueue();

        FeatureFileMapping m2 = new FeatureFileMapping(f2);
        m2.enqueue("/src/File.java", 5, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/File.java", 15, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.buildFromQueue();

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);
        Map<String, List<Pair<String, Integer>>> nestingDepthMap = NestingDepths.getNestingDepthMap(fileMappings);

        ProjectMetrics metrics = new ProjectMetrics(fileMappings, tanglingMap, nestingDepthMap);

        // Verify tangling is captured
        assertNotNull(metrics.getTanglingMap());
        assertTrue(metrics.getTanglingMap().containsKey(f1));
        assertTrue(metrics.getTanglingMap().containsKey(f2));
        assertTrue(metrics.getTanglingMap().get(f1).contains(f2));
        assertTrue(metrics.getTanglingMap().get(f2).contains(f1));
    }

    public void testProjectMetricsWithNesting() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n    F3\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        FeatureModelFeature f3 = features.stream().filter(f -> f.getFeatureName().equals("F3")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);
        assertNotNull(f3);

        // F1: lines 0-30 (depth 1)
        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.enqueue("/src/File.java", 0, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.enqueue("/src/File.java", 30, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.buildFromQueue();

        // F2: lines 5-25 (nested inside F1, depth 2)
        FeatureFileMapping m2 = new FeatureFileMapping(f2);
        m2.enqueue("/src/File.java", 5, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.enqueue("/src/File.java", 25, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.buildFromQueue();

        // F3: lines 10-20 (nested inside F2 and F1, depth 3)
        FeatureFileMapping m3 = new FeatureFileMapping(f3);
        m3.enqueue("/src/File.java", 10, FeatureFileMapping.MarkerType.BEGIN, FeatureFileMapping.AnnotationType.CODE, "o");
        m3.enqueue("/src/File.java", 20, FeatureFileMapping.MarkerType.END, FeatureFileMapping.AnnotationType.CODE, "o");
        m3.buildFromQueue();

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);
        fileMappings.put(ReadAction.compute(f3::getLPQText), m3);

        Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);
        Map<String, List<Pair<String, Integer>>> nestingDepthMap = NestingDepths.getNestingDepthMap(fileMappings);

        ProjectMetrics metrics = new ProjectMetrics(fileMappings, tanglingMap, nestingDepthMap);

        // Average nesting depth: (1 + 2 + 3) / 3 = 2.0
        assertEquals(2.0, metrics.getAvgNestingDepth(), 0.01);
        
        // Verify nesting depth map is accessible
        assertNotNull(metrics.getNestingDepthMap());
        assertEquals(3, metrics.getNestingDepthMap().size());
    }

    public void testProjectMetricsEmptyProject() {
        myFixture.configureByText("model.feature-model", "Root\n");

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = new HashMap<>();
        Map<String, List<Pair<String, Integer>>> nestingDepthMap = new HashMap<>();

        ProjectMetrics metrics = new ProjectMetrics(fileMappings, tanglingMap, nestingDepthMap);

        assertEquals(0, metrics.getNumberOfFeatures());
        assertEquals(0, metrics.getNumberOfAnnotatedFiles());
        assertEquals(0.0, metrics.getAvgScatteringDegree(), 0.01);
        assertEquals(0.0, metrics.getAvgLinesOfFeatureCode(), 0.01);
        assertEquals(0.0, metrics.getAvgNestingDepth(), 0.01);
    }

    public void testProjectMetricsGetFeaturesInProject() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n    F2\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        FeatureModelFeature f2 = features.stream().filter(f -> f.getFeatureName().equals("F2")).findFirst().orElse(null);
        assertNotNull(f1);
        assertNotNull(f2);

        FeatureFileMapping m1 = new FeatureFileMapping(f1);
        m1.enqueue("/src/File.java", 10, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m1.buildFromQueue();

        FeatureFileMapping m2 = new FeatureFileMapping(f2);
        m2.enqueue("/src/File.java", 20, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        m2.buildFromQueue();

        Map<String, FeatureFileMapping> fileMappings = new HashMap<>();
        fileMappings.put(ReadAction.compute(f1::getLPQText), m1);
        fileMappings.put(ReadAction.compute(f2::getLPQText), m2);

        Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap = FeatureTangling.getTanglingMap(getProject(), fileMappings);
        Map<String, List<Pair<String, Integer>>> nestingDepthMap = NestingDepths.getNestingDepthMap(fileMappings);

        ProjectMetrics metrics = new ProjectMetrics(fileMappings, tanglingMap, nestingDepthMap);

        var featuresInProject = metrics.getFeaturesInProject();
        assertEquals(2, featuresInProject.size());
        assertTrue(featuresInProject.contains(f1));
        assertTrue(featuresInProject.contains(f2));
    }
}
