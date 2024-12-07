/**
 Copyright 2024 Tim Sülz

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 **/
package se.isselab.HAnS.settingsPage;

import com.intellij.openapi.options.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.*;
import se.isselab.HAnS.states.ToggleStateService;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsPageBuilder implements Configurable {

    private JCheckBox enableHideAnnotationsCheckbox;
    private JCheckBox enableLoggingCheckbox;
    private boolean initialHideAnnotationsState; // To store the initial state of the checkbox
    private boolean initialLoggingState; // To store the initial state of the logging checkbox
    private Project project; // Reference to the current project

    public SettingsPageBuilder(Project project) {
        this.project = project;
    }

    @Override
    public String getDisplayName() {
        return "HAnS Plugin Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {
        // Creating Base Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel pluginDescriptionLabel = new JLabel("Settings for the HAnS Plugin");
        pluginDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pluginDescriptionLabel);

        // Creating different Tabs
        JBTabbedPane tabbedPane = new JBTabbedPane();

        // General Settings Tab
        JPanel generalTab = new JPanel();
        generalTab.setLayout(new BoxLayout(generalTab, BoxLayout.Y_AXIS));
        tabbedPane.addTab("General", generalTab);

        // Section: Hide Annotations
        SectionBuilder hideAnnotationsSection = new SectionBuilder("Annotations");
        generalTab.add(hideAnnotationsSection.getPanel());

        ToggleStateService toggleService = ToggleStateService.getInstance(project);
        enableHideAnnotationsCheckbox = new JCheckBox("Enable Annotations", toggleService.isEnabled());
        generalTab.add(enableHideAnnotationsCheckbox);

        // Store the initial state of the checkbox
        initialHideAnnotationsState = toggleService.isEnabled();

        enableHideAnnotationsCheckbox.addActionListener(e -> {
            // Mark the setting as modified when checkbox state changes
            // This will trigger the "Apply" button to be enabled
        });

        // Section: Logging
        SectionBuilder loggingSection = new SectionBuilder("Logging");
        generalTab.add(loggingSection.getPanel());

        enableLoggingCheckbox = new JCheckBox("Enable Logging", false); // Default state is false
        generalTab.add(enableLoggingCheckbox);

        // Store the initial state of the logging checkbox
        initialLoggingState = enableLoggingCheckbox.isSelected();

        enableLoggingCheckbox.addActionListener(e -> {
            // Mark the setting as modified when checkbox state changes
            // This will trigger the "Apply" button to be enabled
        });

        // Add the tabbed pane
        panel.add(tabbedPane);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("HAnS Feature Settings");
        panel.setBorder(titledBorder);

        return panel;
    }

    @Override
    public boolean isModified() {
        // Compare current checkbox states with their initial states
        return enableHideAnnotationsCheckbox.isSelected() != initialHideAnnotationsState
                || enableLoggingCheckbox.isSelected() != initialLoggingState;
    }

    @Override
    public void apply() throws ConfigurationException {
        // Apply the changes only when "Apply" is clicked
        ToggleStateService toggleService = ToggleStateService.getInstance(project);
        boolean isSelected = enableHideAnnotationsCheckbox.isSelected();
        toggleService.setEnabled(isSelected, project);

        // Show the restart notification
        showRestartNotification();
        Messages.showInfoMessage("Changes have been applied. Please restart the IDE for the actions to take effect.",
                "Restart Required");
    }

    // Method to show the restart notification
    private void showRestartNotification() {
        Notification notification = new Notification(
                "HAnS Settings",
                "Restart Required",
                "Changes will take effect after restarting the IDE.",
                NotificationType.INFORMATION
        );
        Notifications.Bus.notify(notification);
    }
}
