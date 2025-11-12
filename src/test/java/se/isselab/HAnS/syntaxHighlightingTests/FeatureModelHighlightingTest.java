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
package se.isselab.HAnS.syntaxHighlightingTests;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;

/**
 * Tests for syntax highlighting in feature model files.
 * These tests verify that features, keywords, and syntax elements are properly highlighted.
 */
public class FeatureModelHighlightingTest extends BasePlatformTestCase {

    public void testUnusedFeatureIsHighlighted() {
        // Create the Java file that will be referenced
        myFixture.configureByText("Test.java", "// Java file");
        
        // Create a file that uses only one feature
        myFixture.configureByText("test.feature-to-file",
                """
                        Test.java
                        UsedFeature
                        """);

        // Create a feature model with an unused feature
        // The weak_warning marker indicates where we expect the "Feature is never used" warning
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            UsedFeature
                            <weak_warning descr="Feature is never used">UnusedFeature</weak_warning>
                        """);

        // Check that the highlighting is applied to the feature model
        // Note: checkHighlighting() verifies that all registered annotators run correctly
        myFixture.checkHighlighting(true, false, true);
    }

    public void testAllFeaturesUsedNoWarnings() {
        // Create the Java files that will be referenced
        myFixture.configureByText("TestA.java", "// Java file");
        myFixture.configureByText("TestB.java", "// Java file");
        
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            FeatureA
                            FeatureB
                        """);

        // Create files that use all features
        myFixture.configureByText("test.feature-to-file",
                """
                        TestA.java
                        FeatureA
                        """);

        myFixture.configureByText("test2.feature-to-file",
                """
                        TestB.java
                        FeatureB
                        """);

        // Verify highlighting - no unused feature warnings expected
        myFixture.checkHighlighting(true, false, true);
    }

    public void testFeatureModelSyntaxHighlighting() {
        // Test that basic feature model syntax is highlighted correctly with unused warnings
        myFixture.configureByText("features.feature-model",
                """
                        Root
                            <weak_warning descr="Feature is never used">ParentFeature</weak_warning>
                                <weak_warning descr="Feature is never used">ChildFeature</weak_warning>
                            <weak_warning descr="Feature is never used">AnotherFeature</weak_warning>
                        """);

        // This verifies that the file is parsed correctly and highlighting is applied
        myFixture.checkHighlighting();
    }
}
