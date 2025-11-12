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
 * Tests for AnyContext to ensure live template context detection works correctly.
 * Validates that templates are available in comment contexts and general code contexts.
 */
public class AnyContextTest extends BasePlatformTestCase {

    private AnyContext anyContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        anyContext = new AnyContext();
    }

    public void testContextIsActiveInLineComments() {
        String javaCode = """
                         public class Test {
                             // &begin[Feature]
                             public void method() {}
                             // &end[Feature]
                         }
                         """;
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);
        assertTrue("AnyContext should be active in line comments", anyContext.isInContext(context));
    }

    public void testContextIsActiveInCodeContext() {
        String javaCode = "public class Test {\n    public void method() {}\n}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("method");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);
        assertTrue("AnyContext should be active in code context", anyContext.isInContext(context));
    }

    public void testContextIsActiveInBlockComments() {
        String javaCode = "public class Test { /* &begin[Feature] */ }\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);
        assertTrue("AnyContext should be active in block comments", anyContext.isInContext(context));
    }

    public void testContextAtFileStart() {
        String javaCode = "// &begin[Feature]\npublic class Test {}\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);
        assertTrue("AnyContext should be active at file start", anyContext.isInContext(context));
    }

    public void testContextAtFileEnd() {
        String javaCode = "public class Test {}\n// &end[Feature]\n";
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&end");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);
        assertTrue("AnyContext should be active at file end", anyContext.isInContext(context));
    }

    public void testContextWithMultipleMarkers() {
        String javaCode = """
                public class Test {
                    // &begin[Feature1]
                    public void method1() {}
                    // &end[Feature1]
                    // &begin[Feature2]
                    public void method2() {}
                    // &end[Feature2]
                }
                """;
        myFixture.configureByText("Test.java", javaCode);

        int offset = javaCode.indexOf("&begin");
        TemplateActionContext context = TemplateActionContext.create(
                myFixture.getFile(),
                myFixture.getEditor(),
                offset,
                offset,
                false);
        assertTrue("AnyContext should work with multiple markers", anyContext.isInContext(context));
    }
}

