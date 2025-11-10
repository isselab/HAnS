package se.isselab.HAnS.pluginExtensionsTests;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.metrics.calculators.FeatureScattering;
import se.isselab.HAnS.pluginExtensions.ProjectMetricsService;

public class ProjectMetricsServiceTest extends BasePlatformTestCase {

    public void testFeatureScatteringCallsCalculator() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        FeatureFileMapping mapping = new FeatureFileMapping(feature);

        // Create scattered blocks
        mapping.enqueue("/some/File.java", 0, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");
        mapping.enqueue("/some/File.java", 5, FeatureFileMapping.MarkerType.LINE, FeatureFileMapping.AnnotationType.CODE, "o");

        mapping.buildFromQueue();

        int scattering = FeatureScattering.getScatteringDegree(mapping);
        
        // Expected: two separate segments
        assertEquals(2, scattering);
    }
}
