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

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.trafficLight.HansTrafficLightAction;
import se.isselab.HAnS.trafficLight.HansTrafficLightDashboardModel;

import javax.swing.*;
import java.awt.event.InputEvent;

/**
 * Tests for the Traffic Light Widget UI component.
 * This widget displays feature information in the editor toolbar.
 */
public class TrafficLightWidgetTest extends BasePlatformTestCase {

    private static final Key<HansTrafficLightDashboardModel> DASHBOARD_MODEL = new Key<>("DASHBOARD_MODEL");

    public void testTrafficLightActionIsVisible() {
        // Create a feature model and file annotation
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            TestFeature
                        """);
        myFixture.configureByText("Test.java", "public class Test {}");
        myFixture.configureByText("test.feature-to-file",
                """
                        Test.java
                        TestFeature
                        """);

        // Get the editor
        Editor editor = myFixture.getEditor();
        HansTrafficLightAction action = new HansTrafficLightAction(editor);

        // Create event
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, getProject())
                .add(CommonDataKeys.VIRTUAL_FILE, myFixture.getFile().getVirtualFile())
                .build();

        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.EDITOR_TOOLBAR,
                ActionUiKind.NONE,
                (InputEvent)null
        );

        // Update the action
        action.update(event);

        // Verify the action is visible and enabled
        assertTrue("Traffic light action should be visible", event.getPresentation().isVisible());
        assertTrue("Traffic light action should be enabled", event.getPresentation().isEnabled());
    }

    public void testTrafficLightActionNotVisibleWithoutProject() {
        // Create event without project
        DataContext dataContext = SimpleDataContext.builder().build();

        HansTrafficLightAction action = new HansTrafficLightAction();

        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.EDITOR_TOOLBAR,
                ActionUiKind.NONE,
                (InputEvent)null
        );
        action.update(event);

        // Verify the action is not visible
        assertFalse("Traffic light action should not be visible without project",
                event.getPresentation().isVisible());
    }

    public void testTrafficLightCustomComponentCreation() {
        // Create a feature model
        myFixture.configureByText("test.feature-model", "Root\n");
        myFixture.configureByText("Test.java", "public class Test {}");

        Editor editor = myFixture.getEditor();
        HansTrafficLightAction action = new HansTrafficLightAction(editor);

        Presentation presentation = new Presentation();
        JComponent component = action.createCustomComponent(presentation, ActionPlaces.EDITOR_TOOLBAR);

        // Verify component is created
        assertNotNull("Custom component should be created", component);
        assertTrue("Component should be a JPanel", component instanceof JPanel);
    }

    public void testTrafficLightWidgetRefreshWithEmptyModel() {
        // Create a simple file
        myFixture.configureByText("Test.java", "public class Test {}");

        Editor editor = myFixture.getEditor();
        HansTrafficLightAction action = new HansTrafficLightAction(editor);

        Presentation presentation = new Presentation();
        JComponent component = action.createCustomComponent(presentation, ActionPlaces.EDITOR_TOOLBAR);

        // Create a model with no findings
        HansTrafficLightDashboardModel emptyModel = new HansTrafficLightDashboardModel(true);
        presentation.putClientProperty(DASHBOARD_MODEL, emptyModel);

        // Update the component
        action.updateCustomComponent(component, presentation);

        // Component should still be valid
        assertNotNull("Component should still be valid after refresh", component);
    }

    public void testTrafficLightUpdateThread() {
        HansTrafficLightAction action = new HansTrafficLightAction();
        ActionUpdateThread updateThread = action.getActionUpdateThread();

        // Verify it runs on background thread
        assertEquals("Traffic light should update on background thread",
                ActionUpdateThread.BGT, updateThread);
    }

    public void testTrafficLightActionPerformedDoesNotThrow() {
        // Create test file
        myFixture.configureByText("Test.java", "public class Test {}");

        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PROJECT, getProject())
                .build();

        HansTrafficLightAction action = new HansTrafficLightAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.EDITOR_TOOLBAR,
                ActionUiKind.NONE,
                (InputEvent)null
        );

        // Action should not throw exception (currently does nothing)
        try {
            action.actionPerformed(event);
        } catch (Exception e) {
            fail("actionPerformed should not throw exception: " + e.getMessage());
        }
    }
}
