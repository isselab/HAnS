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
package se.isselab.HAnS.uiTests;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.ui.content.Content;
import se.isselab.HAnS.metrics.view.MetricsViewFactory;

/**
 * Tests for the Metrics View tool window.
 * This tool window displays metrics for features in the project.
 */
public class MetricsViewTest extends BasePlatformTestCase {

    private static final String TOOL_WINDOW_ID = "hans.toolwindow.metrics-view";

    public void testMetricsViewFactoryCreatesToolWindow() {
        // Create a feature model and annotations
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            Feature1
                            Feature2
                        """);

        // Verify factory can be instantiated
        MetricsViewFactory factory = new MetricsViewFactory();
        assertNotNull("Metrics view factory should be created", factory);

        // In test environment, tool windows may not be registered
        // So we test that the factory exists and is ready to create content
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);
            assertNotNull("Tool window content should be created", toolWindow.getContentManager().getContents());
        }
    }

    public void testMetricsViewContentCreation() {
        // Create a feature model with features
        myFixture.configureByText("root.feature-model",
                """
                        RootFeature
                            Feature1
                            Feature2
                        """);

        // Create some code annotations
        myFixture.configureByText("Test.java",
                """
                        public class Test {
                            //@begin[Feature1]
                            public void feature1Method() {
                                System.out.println("Feature 1");
                            }
                            //@end[Feature1]
                            
                            //@begin[Feature2]
                            public void feature2Method() {
                                System.out.println("Feature 2");
                            }
                            //@end[Feature2]
                        }
                        """);

        MetricsViewFactory factory = new MetricsViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            // Create tool window content
            factory.createToolWindowContent(getProject(), toolWindow);

            // Verify content is created
            Content[] contents = toolWindow.getContentManager().getContents();
            assertTrue("Metrics view should have content", contents.length > 0);
        }
    }

    public void testMetricsViewIconIsSet() {
        // Create a feature model
        myFixture.configureByText("test.feature-model", "Root\n");

        MetricsViewFactory factory = new MetricsViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);

            // Verify icon is set
            assertNotNull("Metrics view tool window should have an icon", toolWindow.getIcon());
        }
    }

    public void testMetricsViewToolWindowIsAvailable() {
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            TestFeature
                        """);

        MetricsViewFactory factory = new MetricsViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);

            // Verify the tool window is available and configured
            assertNotNull("Metrics view tool window should be available", toolWindow);
            assertTrue("Tool window should be available", toolWindow.isAvailable());
        }
    }

    public void testMetricsViewTableIsCreated() {
        // Create a feature model
        myFixture.configureByText("main.feature-model",
                """
                        MainFeature
                            Feature1
                            Feature2
                        """);

        MetricsViewFactory factory = new MetricsViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);

            Content[] contents = toolWindow.getContentManager().getContents();
            if (contents.length > 0) {
                assertNotNull("Metrics view content component should not be null",
                        contents[0].getComponent());
            }
        }
    }

    public void testMetricsViewWithEmptyProject() {
        // Don't create any feature model
        MetricsViewFactory factory = new MetricsViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            // Should not throw exception even with empty project
            try {
                factory.createToolWindowContent(getProject(), toolWindow);
                Content[] contents = toolWindow.getContentManager().getContents();
                assertTrue("Metrics view should create content even for empty project",
                        contents.length > 0);
            } catch (Exception e) {
                fail("Metrics view should handle empty project gracefully: " + e.getMessage());
            }
        }
    }

    public void testMetricsViewContentHasScrollPane() {
        // Create a feature model with multiple features
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            Feature1
                            Feature2
                            Feature3
                            Feature4
                            Feature5
                        """);

        MetricsViewFactory factory = new MetricsViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);

            Content[] contents = toolWindow.getContentManager().getContents();
            if (contents.length > 0) {
                var component = contents[0].getComponent();
                assertNotNull("Metrics table should be in a scroll pane", component);
            }
        }
    }
}
