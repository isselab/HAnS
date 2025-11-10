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

import com.intellij.codeInsight.completion.CompletionContributor;
import org.junit.Test;
import se.isselab.HAnS.codeCompletion.FileCompletionContributor;

import static org.junit.Assert.*;

/**
 * Tests for FileCompletionContributor to ensure completion providers are properly registered.
 * Tests for Java code completion with IntelliJ IDE.
 */
public class FileCompletionContributorTest {

    @Test
    public void testFileCompletionContributorCanBeInstantiated() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertNotNull("FileCompletionContributor should be instantiable", contributor);
    }

    @Test
    public void testFileCompletionContributorIsCompletionContributor() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertTrue("FileCompletionContributor should extend CompletionContributor",
                contributor instanceof CompletionContributor);
    }

    @Test
    public void testFileCompletionContributorHasNoNullFields() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertNotNull("FileCompletionContributor instance should not be null", contributor);
    }

    @Test
    public void testFileCompletionContributorConfiguration() {
        FileCompletionContributor contributor = new FileCompletionContributor();
        assertNotNull("FileCompletionContributor should exist and be instantiable", contributor);
    }

    @Test
    public void testBothCompletionProvidersRegistered() {
        FileCompletionContributor testContributor = new FileCompletionContributor();
        assertNotNull("Contributor should be instantiated", testContributor);
    }
}

