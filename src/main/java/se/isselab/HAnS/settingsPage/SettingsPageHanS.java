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
import com.intellij.openapi.util.*;
import com.intellij.ui.components.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsPageHanS implements Configurable {
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "HanS Plugin Settings";
    }

    @Override
    public @Nullable JComponent createComponent() {

        // Creating Base Panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel pluginDescriptionLabel = new JLabel("Settings for the HanS Plugin");
        pluginDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(pluginDescriptionLabel);

        // Creating different Tabs
        JBTabbedPane tabbedPane = new JBTabbedPane();

        // General Settings Tab
        JPanel generalSettingsPanel = new JPanel();
        generalSettingsPanel.setLayout(new BoxLayout(generalSettingsPanel, BoxLayout.Y_AXIS));

        tabbedPane.addTab("General Settings", generalSettingsPanel);


        // Section Hide Annotations
        SectionBuilder hideAnnotationsSection = new SectionBuilder("Hide Annotations"); // Builds the section Hide Annotations
        generalSettingsPanel.add(hideAnnotationsSection.getPanel());// Builds the section Hide Annotations into the generalSettings Panel


        CheckboxBuilder enableHideAnnotationsCheckbox = new CheckboxBuilder("Enable Hide Annotations", "This Enables the Hide Annotation functionality ");// Builds the Checkbox
        generalSettingsPanel.add(enableHideAnnotationsCheckbox.getPanel()); // add the Checkbox in typical IntelliJ Style to the Panel

        // Adding action listener to the checkbox
        enableHideAnnotationsCheckbox.getCheckbox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
             // TODO Add Hide Annotations functionality
            }
        });

        // Section Logging
        SectionBuilder loggingSection = new SectionBuilder("Logging");
        generalSettingsPanel.add(loggingSection.getPanel());


        CheckboxBuilder enableLoggingCheckbox = new CheckboxBuilder("Enable Logging");
        generalSettingsPanel.add(enableLoggingCheckbox.getPanel());


        // Adding action listener to the checkbox
        enableLoggingCheckbox.getCheckbox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO Add logging functionality

            }
        });

        generalSettingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));




        // Clone Settings Tab
        JPanel cloneSettingsPanel = new JPanel();
        cloneSettingsPanel.setLayout(new BoxLayout(cloneSettingsPanel, BoxLayout.Y_AXIS));
        // Section Clone Settings
        SectionBuilder cloneSection = new SectionBuilder("Clone Settings");
        cloneSettingsPanel.add(cloneSection.getPanel());

        CheckboxBuilder enableCloningTraceCheckbox = new CheckboxBuilder("Enable Cloning Trace Tracking");
        cloneSettingsPanel.add(enableCloningTraceCheckbox.getPanel());

        enableCloningTraceCheckbox.getCheckbox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO Clone Trace Tracking Functionality should be implemented here
            }
        });

        CheckboxBuilder showCloneInfoCheckbox = new CheckboxBuilder("Show Clone Information");
        cloneSettingsPanel.add(showCloneInfoCheckbox.getPanel());

        showCloneInfoCheckbox.getCheckbox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO Show Clone Information should be added here

            }
        });

        CheckboxBuilder sourcesSegmentChangesCheckbox = new CheckboxBuilder("Notify on Sources Segment Changes", "Here is the description text");
        cloneSettingsPanel.add(sourcesSegmentChangesCheckbox.getPanel());

        sourcesSegmentChangesCheckbox.getCheckbox().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //TODO add the sources Segment Change functionality

            }
        });

        cloneSettingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        tabbedPane.addTab("Clone Settings", cloneSettingsPanel);

        panel.add(tabbedPane);

        TitledBorder titledBorder = BorderFactory.createTitledBorder("HanS Feature Settings");
        panel.setBorder(titledBorder);

        return panel;
    }


    @Override
    public boolean isModified() {

        return true;
    }

    @Override
    public void apply() throws ConfigurationException {

    }
}

