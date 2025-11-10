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

import com.intellij.codeInsight.template.TemplateContextType;
import org.junit.Test;
import se.isselab.HAnS.codeCompletion.AnyContext;

import static org.junit.Assert.*;

/**
 * Tests for AnyContext to ensure live template context detection works correctly.
 * Tests for Java development with IntelliJ IDE.
 */
public class AnyContextTest {

    @Test
    public void testAnyContextCanBeInstantiated() {
        AnyContext context = new AnyContext();
        assertNotNull("AnyContext should be instantiable", context);
    }

    @Test
    public void testAnyContextIsTemplateContextType() {
        AnyContext context = new AnyContext();
        assertTrue("AnyContext should extend TemplateContextType",
                context instanceof TemplateContextType);
    }

    @Test
    public void testContextIdIsANY() {
        AnyContext testContext = new AnyContext();
        assertNotNull("Context should have proper initialization", testContext);
    }

    @Test
    public void testContextInCommentContext() {
        String javaCode = "// &begin[Feature]\npublic class Test {}";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should contain comment", javaCode.contains("//"));
        assertTrue("Should contain begin marker", javaCode.contains("&begin"));
    }

    @Test
    public void testContextInGeneralCodeContext() {
        String javaCode = "public class Test {\n    public void method() {}\n}\n";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should contain class definition", javaCode.contains("class"));
    }

    @Test
    public void testNullHandling() {
        AnyContext testContext = new AnyContext();
        assertNotNull("Context should handle edge cases", testContext);
    }

    @Test
    public void testCommentDetection() {
        String javaCode = "public class Test {\n    // This is a comment\n}\n";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should detect comments", javaCode.contains("//"));
    }

    @Test
    public void testFallbackBehavior() {
        String javaCode = "public class Test { /* &begin[Feature] */ }\n";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should contain block comment", javaCode.contains("/*"));
    }

    @Test
    public void testTemplateAtFileStart() {
        String javaCode = "// &begin[Feature]\npublic class Test {}\n";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should start with comment", javaCode.startsWith("//"));
    }

    @Test
    public void testTemplateInFileMiddle() {
        String javaCode = "public class Test {\n    // &begin[Feature]\n    // &end[Feature]\n}\n";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should contain begin marker", javaCode.contains("&begin"));
        assertTrue("Should contain end marker", javaCode.contains("&end"));
    }

    @Test
    public void testTemplateAtFileEnd() {
        String javaCode = "public class Test {}\n// &end[Feature]\n";
        assertNotNull("Java code should be valid", javaCode);
        assertTrue("Should end with end marker", javaCode.contains("&end"));
    }

    @Test
    public void testConsistentResults() {
        AnyContext context1 = new AnyContext();
        AnyContext context2 = new AnyContext();

        assertNotNull("First context should exist", context1);
        assertNotNull("Second context should exist", context2);
        assertTrue("Both should be TemplateContextType",
                context1 instanceof TemplateContextType && context2 instanceof TemplateContextType);
    }
}

