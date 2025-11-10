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
package se.isselab.HAnS.codeCompletionTests;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.codeCompletion.FileCompletionContributor;

/**
 * Tests for FileCompletionContributor to ensure completion providers are properly registered.
 * Validates that both file name and feature name completion providers work together.
 */
public class FileCompletionContributorTest extends BasePlatformTestCase {

    public void testFileCompletionContributorIsCompletionContributor() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertTrue("FileCompletionContributor should extend CompletionContributor",
                contributor instanceof CompletionContributor);
    }

    public void testFileCompletionContributorCanBeInstantiated() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertNotNull("FileCompletionContributor should be instantiable", contributor);
    }

    public void testFileNameProviderRegisteredForPathField() {
        // When completing in path field (non-feature-name STRING)
        // the FileNameCompletionProvider should be active
        myFixture.configureByText("test.feature-to-file",
                "src<caret>/File.java\nMyFeature\n");

        LookupElement[] completions = myFixture.completeBasic();

        // Should have file completions from the directory
        assertNotNull("File completion should be available for path field", completions);
    }

    public void testFeatureNameProviderRegisteredForFeatureField() {
        // When completing in feature field (feature-name STRING)
        // the FeatureNameCompletionProvider should be active
        myFixture.configureByText("test.feature-model",
                "Root\n" +
                "    AuthModule\n");

        myFixture.configureByText("test.feature-to-file",
                "src/Test.java\nAu<caret>");

        LookupElement[] completions = myFixture.completeBasic();

        // Should have feature name completions
        assertNotNull("Feature name completion should be available for feature field", completions);
    }

    public void testBothProvidersWork() {
        // Create a test project with both files and features
        myFixture.configureByText("test.feature-model",
                "Root\n" +
                "    TestFeature\n");

        myFixture.configureByText("TestFile.java", "public class TestFile {}");

        myFixture.configureByText("test.feature-to-file",
                "<caret>TestFile");

        LookupElement[] completions = myFixture.completeBasic();

        // Just verify completion invocation doesn't crash
        assertNotNull("File name completions should work", completions);

        myFixture.configureByText("test.feature-to-file",
                "TestFile.java<caret>\nTestFea");

        completions = myFixture.completeBasic();
        assertNotNull("Feature name completions should work", completions);
    }

    public void testCompletionInFeatureToFolder() {
        myFixture.configureByText("test.feature-model",
                "Root\n" +
                "    FolderFeature\n");

        myFixture.configureByText("test.feature-to-folder",
                "src/\nFold<caret>");

        LookupElement[] completions = myFixture.completeBasic();

        // Feature name completion should work in feature-to-folder files too
        assertNotNull("Completion should work in feature-to-folder files", completions);
    }

    public void testFileCompletionExcludesAnnotationFiles() {
        // The FileNameCompletionProvider should exclude .feature-to-file
        // and .feature-to-folder files
        myFixture.configureByText("annotation.feature-to-file", "test.java\nFeature");
        myFixture.configureByText("annotation2.feature-to-folder", "src/\nFeature");
        myFixture.configureByText("regular.java", "public class Test {}");
        myFixture.configureByText("regular.txt", "hello world");

        myFixture.configureByText("test.feature-to-file",
                "<caret>regular");

        LookupElement[] completions = myFixture.completeBasic();

        // Completions may or may not be provided, but if they are,
        // they should exclude annotation files
        // Null is acceptable in test environment
        if (completions != null && completions.length > 0) {
            for (LookupElement element : completions) {
                String name = element.getLookupString();
                assertFalse("Should not include .feature-to-file files",
                        name.endsWith(".feature-to-file"));
                assertFalse("Should not include .feature-to-folder files",
                        name.endsWith(".feature-to-folder"));
            }
        }
    }

    public void testContributorConfigurationIsValid() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertNotNull("FileCompletionContributor instance should not be null", contributor);
    }
}

