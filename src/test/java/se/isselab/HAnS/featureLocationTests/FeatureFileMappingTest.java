package se.isselab.HAnS.featureLocationTests;

import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureFileMapping.FileAnnotationKey;
import se.isselab.HAnS.featureLocation.FeatureFileMapping.MarkerDataBuilder;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.FeatureModelElementFactory;

import java.util.HashMap;
import java.util.Map;

public class FeatureFileMappingTest extends BasePlatformTestCase {

    public void testEnqueueBuildAndLineCounts() {
        FeatureModelFeature feature = FeatureModelElementFactory.createFeature(getProject(), "Root\n    F1");
        SmartPsiElementPointer<FeatureModelFeature> featurePointer = 
                SmartPointerManager.getInstance(getProject()).createSmartPsiElementPointer(feature);

        // Build marker data using the new builder pattern
        Map<FileAnnotationKey, MarkerDataBuilder> markerData = new HashMap<>();

        FileAnnotationKey keyA = new FileAnnotationKey("/path/FileA.java", "originA");
        FileAnnotationKey keyB = new FileAnnotationKey("/path/FileB.java", "originB");

        // Add data for FileA
        MarkerDataBuilder builderA = markerData.computeIfAbsent(keyA,
                k -> new MarkerDataBuilder(FeatureFileMapping.AnnotationType.CODE));
        builderA.addMarker(FeatureFileMapping.MarkerType.LINE, 10);
        builderA.addMarker(FeatureFileMapping.MarkerType.BEGIN, 2);
        builderA.addMarker(FeatureFileMapping.MarkerType.END, 4);

        // Add data for FileB
        MarkerDataBuilder builderB = markerData.computeIfAbsent(keyB,
                k -> new MarkerDataBuilder(FeatureFileMapping.AnnotationType.FILE));
        builderB.addMarker(FeatureFileMapping.MarkerType.NONE, 20);

        // Build the immutable mapping
        FeatureFileMapping mapping = FeatureFileMapping.create(featurePointer, markerData);

        // FileA: lines covered -> 2..4 and 10 => lines {2,3,4,10} -> 4
        assertEquals(4, mapping.getFeatureLineCountInFile(keyA));

        // FileB: NONE created block from 0..20 -> linecount = 21
        assertEquals(21, mapping.getFeatureLineCountInFile(keyB));

        // total should be sum
        int expectedTotal = mapping.getFeatureLineCountInFile(keyA) + mapping.getFeatureLineCountInFile(keyB);
        assertEquals(expectedTotal, mapping.getTotalFeatureLineCount());

        // mapped file paths should include both files
        assertTrue(mapping.getMappedFilePaths().contains("/path/FileA.java"));
        assertTrue(mapping.getMappedFilePaths().contains("/path/FileB.java"));
    }
}

