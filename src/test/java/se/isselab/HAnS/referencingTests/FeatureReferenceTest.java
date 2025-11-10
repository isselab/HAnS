package se.isselab.HAnS.referencingTests;

import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.CodeAnnotationLpq;
import se.isselab.HAnS.featureAnnotation.codeAnnotation.psi.CodeAnnotationElementFactory;
import se.isselab.HAnS.referencing.FeatureReference;

import java.util.List;

public class FeatureReferenceTest extends BasePlatformTestCase {

    public void testResolveFindsFeature() {
        myFixture.configureByText("features.feature-model", "Root\n    ResolveFeature\n");

        List<FeatureModelFeature> features = FeatureModelUtil.findFeatures(getProject());
        assertFalse(features.isEmpty());

        CodeAnnotationLpq lpq = CodeAnnotationElementFactory.createLPQ(getProject(), "ResolveFeature");
        assertNotNull(lpq);

        FeatureReference ref = new FeatureReference(lpq, TextRange.from(0, lpq.getTextLength()));
        assertNotNull(ref.resolve());
    }
}
