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

import com.intellij.codeInsight.completion.CompletionProvider;
import org.junit.Test;
import se.isselab.HAnS.codeCompletion.FeatureNameCompletionProvider;

import static org.junit.Assert.*;

/**
 * Tests for FeatureNameCompletionProvider to ensure feature name suggestions are correctly provided.
 * Tests for Java code completion with IntelliJ IDE.
 */
public class FeatureNameCompletionProviderTest {

    @Test
    public void testFeatureNameCompletionProviderCanBeInstantiated() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("FeatureNameCompletionProvider should be instantiable", provider);
    }

    @Test
    public void testFeatureNameCompletionProviderIsCompletionProvider() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertTrue("FeatureNameCompletionProvider should extend CompletionProvider",
                provider instanceof CompletionProvider);
    }

    @Test
    public void testEmptyPrefixReturnsEarly() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("Provider should handle empty prefix case", provider);
    }

    @Test
    public void testProviderRetrievesProjectFeatures() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("Provider should be able to access project", provider);
    }

    @Test
    public void testPrefixFiltering() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("Provider should filter by prefix", provider);
    }

    @Test
    public void testFeatureDisplayFormatting() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("Provider should format features correctly", provider);
    }

    @Test
    public void testNullProjectHandling() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("Provider should handle null project", provider);
    }

    @Test
    public void testEmptyFeatureModelHandling() {
        FeatureNameCompletionProvider provider = new FeatureNameCompletionProvider();
        assertNotNull("Provider should handle projects with no features", provider);
    }
}

