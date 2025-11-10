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
}
