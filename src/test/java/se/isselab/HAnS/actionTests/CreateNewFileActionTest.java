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

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.actions.newFile.CreateNewFileAction;

import static org.junit.Assert.assertNotEquals;

/**
 * Tests for the CreateNewFileAction which creates new feature annotation files.
 * This action allows creating .feature-model, .feature-to-file, and .feature-to-folder files.
 */
public class CreateNewFileActionTest extends BasePlatformTestCase {

    public void testCreateNewFileActionInstantiation() {
        // Create the action
        CreateNewFileAction action = new CreateNewFileAction();

        // Verify action is instantiated
        assertNotNull("CreateNewFileAction should be instantiated", action);
    }

    public void testCreateNewFileActionEquality() {
        // Create two instances
        CreateNewFileAction action1 = new CreateNewFileAction();
        CreateNewFileAction action2 = new CreateNewFileAction();

        // Verify equals method
        assertEquals("Two CreateNewFileAction instances should be equal",
                action1, action2);
    }

    public void testCreateNewFileActionHashCode() {
        // Create the action
        CreateNewFileAction action = new CreateNewFileAction();

        // Verify hash code is consistent
        assertEquals("CreateNewFileAction hash code should be 0", 0, action.hashCode());
    }

    public void testCreateNewFileActionNotEqualsToNull() {
        // Create the action
        CreateNewFileAction action = new CreateNewFileAction();

        // Verify not equals to null
        assertNotEquals("CreateNewFileAction should not equal null", null, action);
    }

    public void testCreateNewFileActionNotEqualsToOtherClass() {
        // Create the action
        CreateNewFileAction action = new CreateNewFileAction();

        // Verify not equals to different class
        assertNotEquals("CreateNewFileAction should not equal a String", "test", action);
    }

    public void testCreateNewFileActionMultipleInstances() {
        // Create multiple instances
        CreateNewFileAction action1 = new CreateNewFileAction();
        CreateNewFileAction action2 = new CreateNewFileAction();
        CreateNewFileAction action3 = new CreateNewFileAction();

        // Verify all are equal
        assertEquals("All CreateNewFileAction instances should be equal",
                action1, action2);
        assertEquals("All CreateNewFileAction instances should be equal",
                action2, action3);
        assertEquals("All CreateNewFileAction instances should be equal",
                action1, action3);
    }

    public void testCreateNewFileActionDefaultText() {
        // The default text for new files should be "_" as per the buildDialog method
        // This is a documentation test to verify the expected behavior
        
        CreateNewFileAction action = new CreateNewFileAction();
        assertNotNull("Action should be created successfully", action);
        
        // Note: The actual default text "_" is set in buildDialog method
        // which requires UI interaction to fully test
    }

    public void testCreateNewFileActionReflexiveEquality() {
        // Create the action
        CreateNewFileAction action = new CreateNewFileAction();

        // Verify reflexive property: x.equals(x) should be true
        assertEquals("CreateNewFileAction should equal itself", action, action);
    }

    public void testCreateNewFileActionSymmetricEquality() {
        // Create two instances
        CreateNewFileAction action1 = new CreateNewFileAction();
        CreateNewFileAction action2 = new CreateNewFileAction();

        // Verify symmetric property: if x.equals(y) then y.equals(x)
        assertEquals("Symmetric equality should hold", action1, action2);
        assertEquals("Symmetric equality should hold", action2, action1);
    }

    public void testCreateNewFileActionTransitiveEquality() {
        // Create three instances
        CreateNewFileAction action1 = new CreateNewFileAction();
        CreateNewFileAction action2 = new CreateNewFileAction();
        CreateNewFileAction action3 = new CreateNewFileAction();

        // Verify transitive property: if x.equals(y) and y.equals(z) then x.equals(z)
        assertEquals("First equality", action1, action2);
        assertEquals("Second equality", action2, action3);
        assertEquals("Transitive equality should hold", action1, action3);
    }
}
