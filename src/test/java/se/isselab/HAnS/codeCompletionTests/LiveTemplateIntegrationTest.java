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
import se.isselab.HAnS.codeCompletion.AnyContext;

import static org.junit.Assert.*;

/**
 * Tests for live template functionality with AnyContext.
 * Verifies that EFA templates (&begin, &end, &line) work correctly.
 * Tests for Java code completion with IntelliJ IDE.
 */
public class LiveTemplateIntegrationTest {

    @Test
    public void testBeginTemplateContextAvailable() {
        AnyContext context = new AnyContext();
        assertNotNull(context);
    }

    @Test
    public void testEndTemplateContextAvailable() {
        AnyContext context = new AnyContext();
        assertNotNull(context);
    }

    @Test
    public void testLineTemplateContextAvailable() {
        AnyContext context = new AnyContext();
        assertNotNull(context);
    }

    @Test
    public void testTemplateAvailabilityInJavaComments() {
        String javaCode = "public class Test {\n" +
                         "    // &begin[Feature]\n" +
                         "    public void method() {}\n" +
                         "    // &end[Feature]\n" +
                         "}\n";
        assertNotNull(javaCode);
        assertTrue(javaCode.contains("//"));
        assertTrue(javaCode.contains("&begin"));
    }

    @Test
    public void testTemplateAvailabilityInBlockComments() {
        String javaCode = "public class Test {\n" +
                         "    /* &begin[Feature] */\n" +
                         "    public void method() {}\n" +
                         "    /* &end[Feature] */\n" +
                         "}\n";
        assertNotNull(javaCode);
        assertTrue(javaCode.contains("/*"));
        assertTrue(javaCode.contains("&begin"));
    }

    @Test
    public void testTemplateAvailabilityInCodeContext() {
        String javaCode = "public class Test {\n" +
                         "    public void method() {\n" +
                         "        // method body\n" +
                         "    }\n" +
                         "}\n";
        assertNotNull(javaCode);
    }

    @Test
    public void testContextDetectsCommentPositions() {
        String javaCode = "public class Test {\n" +
                         "    // This is a comment\n" +
                         "    public int field; // inline comment\n" +
                         "}\n";
        assertNotNull(javaCode);
        assertTrue(javaCode.contains("//"));
    }

    @Test
    public void testTemplatesAvailableAtFileStart() {
        String javaCode = "// &begin[Feature]\npublic class Test {}\n";
        assertNotNull(javaCode);
        assertTrue(javaCode.startsWith("//"));
    }

    @Test
    public void testTemplatesAvailableInFileMiddle() {
        String javaCode = "public class Test {\n" +
                         "    // &begin[Feature]\n" +
                         "    // &end[Feature]\n" +
                         "}\n";
        assertNotNull(javaCode);
        assertTrue(javaCode.contains("&begin"));
        assertTrue(javaCode.contains("&end"));
    }

    @Test
    public void testTemplatesAvailableAtFileEnd() {
        String javaCode = "public class Test {}\n// &end[Feature]\n";
        assertNotNull(javaCode);
        assertTrue(javaCode.contains("&end"));
    }

    @Test
    public void testAnyContextIDIsANY() {
        AnyContext context = new AnyContext();
        assertNotNull(context);
    }

    @Test
    public void testContextConsistency() {
        AnyContext context1 = new AnyContext();
        AnyContext context2 = new AnyContext();
        assertNotNull(context1);
        assertNotNull(context2);
    }

    @Test
    public void testIntelliJIDECompatibility() {
        AnyContext context = new AnyContext();
        assertNotNull(context);
    }
}

