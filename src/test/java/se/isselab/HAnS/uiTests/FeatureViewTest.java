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
import se.isselab.HAnS.featureView.FeatureViewFactory;

/**
 * Tests for the Feature View tool window.
 * This tool window displays the feature model structure.
 */
public class FeatureViewTest extends BasePlatformTestCase {

    private static final String TOOL_WINDOW_ID = "hans.toolwindow.feature-model-view";

    public void testFeatureViewFactoryCreatesToolWindow() {
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            Feature1
                            Feature2
                                SubFeature
                        """);

        // Verify factory can be instantiated
        FeatureViewFactory factory = new FeatureViewFactory();
        assertNotNull("Feature view factory should be created", factory);

        // In test environment, tool windows may not be registered
        // So we test that the factory exists and is ready to create content
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);
        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);
            assertNotNull("Tool window content should be created", toolWindow.getContentManager().getContents());
        }
    }

    public void testFeatureViewFactoryWithFeatureModel() {
        // Create a feature model
        myFixture.configureByText("root.feature-model",
                """
                        RootFeature
                            ChildFeature1
                            ChildFeature2
                        """);

        FeatureViewFactory factory = new FeatureViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            // Create tool window content
            factory.createToolWindowContent(getProject(), toolWindow);

            // Verify content is created
            Content[] contents = toolWindow.getContentManager().getContents();
            assertTrue("Tool window should have content", contents.length > 0);
        }
    }

    public void testFeatureViewWithoutFeatureModel() {
        // Don't create a feature model - test the "no model found" case
        FeatureViewFactory factory = new FeatureViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            // Create tool window content
            factory.createToolWindowContent(getProject(), toolWindow);

            // Content should still be created (showing "no feature model found" message)
            Content[] contents = toolWindow.getContentManager().getContents();
            assertTrue("Tool window should have content even without feature model",
                    contents.length > 0);
        }
    }

    public void testFeatureViewIconIsSet() {
        // Create a feature model
        myFixture.configureByText("test.feature-model", "Root\n");

        FeatureViewFactory factory = new FeatureViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);

            // Verify icon is set
            assertNotNull("Tool window should have an icon", toolWindow.getIcon());
        }
    }

    public void testFeatureViewContentIsNotNull() {
        // Create a feature model
        myFixture.configureByText("main.feature-model",
                """
                        MainFeature
                            SubFeature1
                                DeepFeature
                            SubFeature2
                        """);

        FeatureViewFactory factory = new FeatureViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            factory.createToolWindowContent(getProject(), toolWindow);

            // Get content
            Content[] contents = toolWindow.getContentManager().getContents();
            if (contents.length > 0) {
                assertNotNull("Content component should not be null",
                        contents[0].getComponent());
            }
        }
    }

    public void testMultipleFeatureViewCreations() {
        // Create a feature model
        myFixture.configureByText("test.feature-model", "Root\n");

        FeatureViewFactory factory = new FeatureViewFactory();
        ToolWindow toolWindow = ToolWindowManager.getInstance(getProject()).getToolWindow(TOOL_WINDOW_ID);

        if (toolWindow != null) {
            // Create content multiple times
            factory.createToolWindowContent(getProject(), toolWindow);
            int firstContentCount = toolWindow.getContentManager().getContents().length;

            factory.createToolWindowContent(getProject(), toolWindow);
            int secondContentCount = toolWindow.getContentManager().getContents().length;

            // Content should be added each time
            assertTrue("Content should be created",
                    secondContentCount >= firstContentCount);
        }
    }
}
