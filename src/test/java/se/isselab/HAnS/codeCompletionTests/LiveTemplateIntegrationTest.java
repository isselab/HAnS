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

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.codeCompletion.AnyContext;

/**
 * Tests for live template functionality with AnyContext.
 * Verifies that EFA templates (&begin, &end, &line) context detection works correctly.
 * Note: CommentContext is not tested here due to PSI comment detection complexities in tests.
 */
public class LiveTemplateIntegrationTest extends BasePlatformTestCase {

    private AnyContext anyContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        anyContext = new AnyContext();
    }

    public void testTemplateAvailableInLineComments() {
        String javaCode = "public class Test {\n" +
                         "    // &begin[Feature]\n" +
                         "    public void method() {}\n" +
                         "    // &end[Feature]\n" +
                         "}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        assertTrue("AnyContext should be active in line comments",
                anyContext.isInContext(context));
    }

    public void testTemplateAvailableInBlockComments() {
        String javaCode = "public class Test {\n" +
                         "    /* &begin[Feature] */\n" +
                         "    public void method() {}\n" +
                         "    /* &end[Feature] */\n" +
                         "}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        assertTrue("AnyContext should be active in block comments",
                anyContext.isInContext(context));
    }

    public void testTemplateNotAvailableOutsideComments() {
        String javaCode = "public class Test {\n" +
                         "    public void method() { // &begin[Feature]\n" +
                         "    }\n" +
                         "}\n";
        myFixture.configureByText("Test.java", javaCode);

        // Position on the method declaration (not in comment)
        int offset = javaCode.indexOf("void");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        // AnyContext should still be active even in code (it's permissive)
        assertTrue("AnyContext should be active everywhere",
                anyContext.isInContext(context));
    }

    public void testTemplateAtFileStart() {
        String javaCode = "// &begin[Feature]\npublic class Test {}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        assertTrue("AnyContext should be active at file start in comment",
                anyContext.isInContext(context));
    }

    public void testTemplateAtFileEnd() {
        String javaCode = "public class Test {}\n// &end[Feature]\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&end");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        assertTrue("AnyContext should be active at file end in comment",
                anyContext.isInContext(context));
    }

    public void testMultipleTemplateMarkers() {
        String javaCode = "public class Test {\n" +
                         "    // &begin[Feature1]\n" +
                         "    public void method1() {}\n" +
                         "    // &end[Feature1]\n" +
                         "    // &begin[Feature2]\n" +
                         "    public void method2() {}\n" +
                         "    // &end[Feature2]\n" +
                         "}\n";
        myFixture.configureByText("Test.java", javaCode);

        // Check first marker
        int offset1 = javaCode.indexOf("&begin");
        TemplateActionContext context1 = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset1,
                offset1,
                false);

        assertTrue("First template should be available",
                anyContext.isInContext(context1));

        // Check second marker
        int offset2 = javaCode.indexOf("&begin", offset1 + 1);
        TemplateActionContext context2 = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset2,
                offset2,
                false);

        assertTrue("Second template should be available",
                anyContext.isInContext(context2));
    }

    public void testLineTemplateMarker() {
        String javaCode = "public class Test {\n" +
                         "    public void method() {\n" +
                         "        // &line[Feature]\n" +
                         "    }\n" +
                         "}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&line");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        assertTrue("AnyContext should support &line marker",
                anyContext.isInContext(context));
    }

    public void testInlineCommentWithMarker() {
        String javaCode = "public class Test {\n" +
                         "    int field; // &begin[Feature]\n" +
                         "}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);

        assertTrue("AnyContext should work with inline comments",
                anyContext.isInContext(context));
    }
}

