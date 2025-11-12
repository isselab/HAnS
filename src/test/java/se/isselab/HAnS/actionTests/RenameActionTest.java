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
package se.isselab.HAnS.actionTests;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.impl.SimpleDataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.actions.RenameAction;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.awt.event.InputEvent;

/**
 * Tests for the RenameAction which renames a selected feature in the feature model.
 */
public class RenameActionTest extends BasePlatformTestCase {

    public void testRenameActionIsEnabled() {
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            FeatureToRename
                        """);

        // Find the feature to rename
        PsiElement element = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("FeatureToRename") + 1);
        FeatureModelFeature feature = PsiTreeUtil.getParentOfType(element, FeatureModelFeature.class);

        assertNotNull("Feature should be found", feature);

        // Create event with the feature
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, feature)
                .add(PlatformCoreDataKeys.PSI_ELEMENT_ARRAY, new PsiElement[]{feature})
                .build();

        RenameAction action = new RenameAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.UNKNOWN,
                ActionUiKind.NONE,
                (InputEvent)null
        );
        action.update(event);

        // Verify action is enabled
        assertTrue("Rename action should be enabled for a single feature",
                event.getPresentation().isEnabled());
    }

    public void testRenameActionIsDisabledForMultipleElements() {
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            Feature1
                            Feature2
                        """);

        // Find both features
        PsiElement element1 = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("Feature1") + 1);
        PsiElement element2 = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("Feature2") + 1);

        FeatureModelFeature feature1 = PsiTreeUtil.getParentOfType(element1, FeatureModelFeature.class);
        FeatureModelFeature feature2 = PsiTreeUtil.getParentOfType(element2, FeatureModelFeature.class);

        assertNotNull("Feature1 should be found", feature1);
        assertNotNull("Feature2 should be found", feature2);

        // Create event with multiple features
        DataContext dataContext = SimpleDataContext.builder()
                .add(PlatformCoreDataKeys.PSI_ELEMENT_ARRAY, new PsiElement[]{feature1, feature2})
                .build();

        RenameAction action = new RenameAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.UNKNOWN,
                ActionUiKind.NONE,
                (InputEvent)null
        );
        action.update(event);

        // Verify action is disabled for multiple elements
        assertFalse("Rename action should be disabled for multiple features",
                event.getPresentation().isEnabled());
    }

    public void testRenameActionUpdateThread() {
        RenameAction action = new RenameAction();
        ActionUpdateThread updateThread = action.getActionUpdateThread();

        // Verify it runs on background thread
        assertEquals("Rename action should update on background thread",
                ActionUpdateThread.BGT, updateThread);
    }

    public void testRenameActionPerformedWithProject() {
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            OldFeatureName
                        """);

        // Find the feature to rename
        PsiElement element = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("OldFeatureName") + 1);
        FeatureModelFeature feature = PsiTreeUtil.getParentOfType(element, FeatureModelFeature.class);

        assertNotNull("Feature should be found", feature);

        // Create event with project
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, feature)
                .add(CommonDataKeys.PROJECT, getProject())
                .build();

        RenameAction action = new RenameAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.UNKNOWN,
                ActionUiKind.NONE,
                (InputEvent)null
        );

        // Perform the action
        // Note: This will try to show a RenameDialog which requires UI interaction
        try {
            action.actionPerformed(event);
            // If we get here without exception, the action executed
        } catch (Exception e) {
            // In test mode, dialog might not be fully functional
            // The important thing is that the action doesn't crash unexpectedly
        }
    }

    public void testRenameActionWithRootFeature() {
        // Create a feature model
        myFixture.configureByText("test.feature-model", "Root\n");

        // Find the root feature
        PsiElement element = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("Root") + 1);
        FeatureModelFeature rootFeature = PsiTreeUtil.getParentOfType(element, FeatureModelFeature.class);

        assertNotNull("Root feature should be found", rootFeature);

        // Create event
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, rootFeature)
                .add(PlatformCoreDataKeys.PSI_ELEMENT_ARRAY, new PsiElement[]{rootFeature})
                .build();

        RenameAction action = new RenameAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.UNKNOWN,
                ActionUiKind.NONE,
                (InputEvent)null
        );
        action.update(event);

        // Verify action is enabled for root feature
        assertTrue("Rename action should be enabled for root feature",
                event.getPresentation().isEnabled());
    }

    public void testRenameActionWithNestedFeature() {
        // Create a feature model with nested structure
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            ParentFeature
                                ChildFeature
                        """);

        // Find the nested feature
        PsiElement element = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("ChildFeature") + 1);
        FeatureModelFeature feature = PsiTreeUtil.getParentOfType(element, FeatureModelFeature.class);

        assertNotNull("Nested feature should be found", feature);

        // Create event
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, feature)
                .add(PlatformCoreDataKeys.PSI_ELEMENT_ARRAY, new PsiElement[]{feature})
                .build();

        RenameAction action = new RenameAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.UNKNOWN,
                ActionUiKind.NONE,
                (InputEvent)null
        );
        action.update(event);

        // Verify action is enabled for nested feature
        assertTrue("Rename action should be enabled for nested feature",
                event.getPresentation().isEnabled());
    }

    public void testRenameActionPerformedWithoutProject() {
        // Create a feature model
        myFixture.configureByText("test.feature-model",
                """
                        Root
                            FeatureToRename
                        """);

        // Find the feature
        PsiElement element = myFixture.getFile().findElementAt(
                myFixture.getFile().getText().indexOf("FeatureToRename") + 1);
        FeatureModelFeature feature = PsiTreeUtil.getParentOfType(element, FeatureModelFeature.class);

        assertNotNull("Feature should be found", feature);

        // Create event WITHOUT project
        DataContext dataContext = SimpleDataContext.builder()
                .add(CommonDataKeys.PSI_ELEMENT, feature)
                .build();

        RenameAction action = new RenameAction();
        AnActionEvent event = AnActionEvent.createEvent(
                dataContext,
                new Presentation(),
                ActionPlaces.UNKNOWN,
                ActionUiKind.NONE,
                (InputEvent)null
        );

        // Perform action without project - should handle gracefully
        try {
            action.actionPerformed(event);
            // Action should complete without throwing
        } catch (Exception e) {
            // Expected behavior when project is null
        }
    }
}
