/*
Copyright 2025 Johan Martinson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package se.isselab.HAnS.intentionTests;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

import java.util.List;

/**
 * Tests for the UnassignedFeatureQuickFix intention action.
 * This intention allows users to quickly add undefined features to the feature model.
 */
public class UnassignedFeatureQuickFixTest extends BasePlatformTestCase {

    public void testQuickFixIsAvailableForUnassignedFeature() {
        // Create a feature model without the feature
        myFixture.configureByText("test.feature-model", "Root\n");

        // Create a file annotation that references a non-existent feature
        myFixture.configureByText("test.feature-to-file",
                """
                        Test.java
                        UndefinedFeature
                        """);

        // Trigger highlighting to run annotators which register the quick fix
        myFixture.doHighlighting();

        // Check that the quick fix is available
        List<IntentionAction> intentions = myFixture.getAllQuickFixes();
        
        // Verify that at least one intention is available
        assertFalse("Should have quick fixes available for undefined feature", intentions.isEmpty());
        
        // Look for our specific quick fix
        boolean hasUnassignedQuickFix = intentions.stream()
                .anyMatch(i -> i.getFamilyName().contains("Add new feature in the Feature Model"));
        
        assertTrue("Should have UnassignedFeature quick fix available", hasUnassignedQuickFix);
    }

    public void testQuickFixTextContainsFeatureName() {
        // Create a feature model
        myFixture.configureByText("features.feature-model", "Root\n");

        // Create annotation with undefined feature
        myFixture.configureByText("test.feature-to-file",
                """
                        Test.java
                        MyNewFeature
                        """);

        // Trigger highlighting to run annotators
        myFixture.doHighlighting();

        // Get all available intentions
        List<IntentionAction> intentions = myFixture.getAllQuickFixes();

        // Find the quick fix and verify its text includes the feature name
        IntentionAction quickFix = intentions.stream()
                .filter(i -> i.getFamilyName().contains("Add new feature in the Feature Model"))
                .findFirst()
                .orElse(null);

        if (quickFix != null) {
            String text = quickFix.getText();
            assertTrue("Quick fix text should contain feature name: " + text,
                    text.contains("MyNewFeature"));
        }
    }

    public void testNoQuickFixForDefinedFeature() {
        // Create a feature model with the feature already defined
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            ExistingFeature
                        """);

        // Create annotation using the defined feature
        myFixture.configureByText("test.feature-to-file",
                """
                        Test.java
                        ExistingFeature
                        """);

        // Trigger highlighting to run annotators
        myFixture.doHighlighting();

        // Get all intentions
        List<IntentionAction> intentions = myFixture.getAllQuickFixes();

        // Verify no UnassignedFeature quick fix is available
        boolean hasUnassignedQuickFix = intentions.stream()
                .anyMatch(i -> i.getFamilyName().contains("Add new feature in the Feature Model"));

        assertFalse("Should not have UnassignedFeature quick fix for defined feature", 
                hasUnassignedQuickFix);
    }
}
