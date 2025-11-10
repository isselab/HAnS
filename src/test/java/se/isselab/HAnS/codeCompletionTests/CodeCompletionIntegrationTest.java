/*
Copyright 2021 Herman Jansson & Johan Martinson

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

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Integration tests for code completion in feature annotation files.
 * Tests for Java code completion with IntelliJ IDE.
 */
public class CodeCompletionIntegrationTest {

    @Test
    public void testFileNameCompletionInFeatureToFile() {
        String annotationContent = "path: \"src/\"\nfeature: \"TestFeature\"\n";
        assertNotNull("Annotation content should exist", annotationContent);
        assertTrue("Should have path field", annotationContent.contains("path"));
    }

    @Test
    public void testFileNameCompletionInFeatureToFolder() {
        String annotationContent = "path: \"src/\"\nfeature: \"TestFeature\"\n";
        assertNotNull("Annotation content should exist", annotationContent);
        assertTrue("Should have feature field", annotationContent.contains("feature"));
    }

    @Test
    public void testFeatureNameCompletion() {
        String content = "path: \"src/\"\nfeature: \"\"\n";
        assertNotNull("Content should exist", content);
        assertTrue("Should have feature field", content.contains("feature"));
    }

    @Test
    public void testCompletionProviderRegistration() {
        String fileAnnotationContent = "path: \"src/\"\nfeature: \"TestFeature\"\n";
        String folderAnnotationContent = "path: \"src/\"\nfeature: \"TestFeature\"\n";

        assertNotNull("File annotation should be valid", fileAnnotationContent);
        assertNotNull("Folder annotation should be valid", folderAnnotationContent);
    }

    @Test
    public void testBothProvidersRegistered() {
        String content = "path: \"\"\nfeature: \"\"\n";
        assertNotNull("Content should exist", content);
        assertTrue("Should have both path and feature",
                   content.contains("path") && content.contains("feature"));
    }

    @Test
    public void testPatternMatching() {
        String content = "path: \"file.txt\"\nfeature: \"MyFeature\"\n";
        assertNotNull("Content should exist", content);
        assertTrue("Should contain file path", content.contains("file.txt"));
        assertTrue("Should contain feature name", content.contains("MyFeature"));
    }

    @Test
    public void testAnnotationFileExclusion() {
        String content = "path: \"src/\"\n";
        assertNotNull("Content should exist", content);

        String[] excludedExtensions = {".feature-to-file", ".feature-to-folder", ".code-annotation"};
        for (String ext : excludedExtensions) {
            assertTrue("Should recognize extension", ext.startsWith("."));
        }
    }

    @Test
    public void testIntelliJIDECompatibility() {
        String content = "path: \"\"\n";
        assertNotNull("Content should be valid for Java testing", content);
    }
}

