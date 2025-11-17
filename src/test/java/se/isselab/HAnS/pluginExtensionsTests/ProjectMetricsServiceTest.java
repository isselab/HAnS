package se.isselab.HAnS.pluginExtensionsTests;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
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

        // Create SmartPsiElementPointer
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);

        // Build marker data map
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        
        // Create scattered blocks
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/some/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 0);
        builder.addMarker(FeatureFileMapping.MarkerType.LINE, 5);

        // Create immutable FeatureFileMapping
        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerDataMap);

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        
        // Expected: two separate segments
        assertEquals(2, scattering);
    }

    public void testProjectMetricsWithSingleFeature() {
        myFixture.configureByText("model.feature-model", "Root\n    F1\n");

        List<FeatureModelFeature> features = ReadAction.compute(() -> FeatureModelUtil.findFeatures(getProject()));
        FeatureModelFeature f1 = features.stream().filter(f -> f.getFeatureName().equals("F1")).findFirst().orElse(null);
        assertNotNull(f1);

        // Create SmartPsiElementPointer
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);

        // Build marker data map
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap = new HashMap<>();
        
        FeatureFileMapping.FileAnnotationKey key = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder = markerDataMap.computeIfAbsent(key, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder.addMarker(FeatureFileMapping.MarkerType.BEGIN, 10);
        builder.addMarker(FeatureFileMapping.MarkerType.END, 20);

        // Create immutable FeatureFileMapping
        FeatureFileMapping m1 = FeatureFileMapping.create(featurePointer, markerDataMap);

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
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File1.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 99);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        // F2: scattered across 3 files (scattering = 3)
        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        
        FeatureFileMapping.FileAnnotationKey key2a = new FeatureFileMapping.FileAnnotationKey("/src/File1.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2a = markerDataMap2.computeIfAbsent(key2a, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2a.addMarker(FeatureFileMapping.MarkerType.LINE, 10);
        
        FeatureFileMapping.FileAnnotationKey key2b = new FeatureFileMapping.FileAnnotationKey("/src/File2.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2b = markerDataMap2.computeIfAbsent(key2b, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2b.addMarker(FeatureFileMapping.MarkerType.LINE, 20);
        
        FeatureFileMapping.FileAnnotationKey key2c = new FeatureFileMapping.FileAnnotationKey("/src/File3.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2c = markerDataMap2.computeIfAbsent(key2c, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2c.addMarker(FeatureFileMapping.MarkerType.LINE, 30);
        
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        // F3: two segments in File2.java (scattering = 2)
        SmartPsiElementPointer<FeatureModelFeature> f3Pointer = 
            SmartPointerManager.getInstance(f3.getProject()).createSmartPsiElementPointer(f3);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap3 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key3 = new FeatureFileMapping.FileAnnotationKey("/src/File2.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder3 = markerDataMap3.computeIfAbsent(key3, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder3.addMarker(FeatureFileMapping.MarkerType.LINE, 10);
        builder3.addMarker(FeatureFileMapping.MarkerType.LINE, 50);
        FeatureFileMapping m3 = FeatureFileMapping.create(f3Pointer, markerDataMap3);

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
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 10);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 5);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 15);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

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
        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.BEGIN, 0);
        builder1.addMarker(FeatureFileMapping.MarkerType.END, 30);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        // F2: lines 5-25 (nested inside F1, depth 2)
        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.BEGIN, 5);
        builder2.addMarker(FeatureFileMapping.MarkerType.END, 25);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

        // F3: lines 10-20 (nested inside F2 and F1, depth 3)
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

        SmartPsiElementPointer<FeatureModelFeature> f1Pointer = 
            SmartPointerManager.getInstance(f1.getProject()).createSmartPsiElementPointer(f1);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap1 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key1 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder1 = markerDataMap1.computeIfAbsent(key1, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder1.addMarker(FeatureFileMapping.MarkerType.LINE, 10);
        FeatureFileMapping m1 = FeatureFileMapping.create(f1Pointer, markerDataMap1);

        SmartPsiElementPointer<FeatureModelFeature> f2Pointer = 
            SmartPointerManager.getInstance(f2.getProject()).createSmartPsiElementPointer(f2);
        Map<FeatureFileMapping.FileAnnotationKey, FeatureFileMapping.MarkerDataBuilder> markerDataMap2 = new HashMap<>();
        FeatureFileMapping.FileAnnotationKey key2 = new FeatureFileMapping.FileAnnotationKey("/src/File.java", "o");
        FeatureFileMapping.MarkerDataBuilder builder2 = markerDataMap2.computeIfAbsent(key2, 
            k -> new FeatureFileMapping.MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builder2.addMarker(FeatureFileMapping.MarkerType.LINE, 20);
        FeatureFileMapping m2 = FeatureFileMapping.create(f2Pointer, markerDataMap2);

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
