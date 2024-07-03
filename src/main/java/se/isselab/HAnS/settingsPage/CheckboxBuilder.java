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

import com.intellij.ui.*;
import com.intellij.util.ui.*;

import javax.swing.*;
import java.awt.*;

public class CheckboxBuilder {

        private final JPanel panel;
        private  final JCheckBox checkBox;

         private final int DISTANCE = 5;
    /** Checkbox Builder builds a panel in the form of the base IntelliJ Layout with a checkbox and an optional  **/
    public CheckboxBuilder(String checkboxText, String descriptionText) {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for more control

        panel.setMaximumSize(new Dimension(1000, 55));
        panel.setOpaque(true);




        checkBox = new JCheckBox(checkboxText);

        // Create GridBagConstraints for the checkbox
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Set the column position
        gbc.gridy = 0; // Set the row position
        gbc.anchor = GridBagConstraints.NORTHWEST;// Align to the left
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.insets = JBUI.insets(5, DISTANCE+5, 5, DISTANCE); // Add some padding
        panel.add(checkBox, gbc); // Add checkbox with constraints

        GridBagConstraints gbclabel = new GridBagConstraints();
        gbclabel.gridx = 0;
        gbclabel.gridy = 1;
        gbclabel.anchor = GridBagConstraints.NORTHWEST;
        gbclabel.insets = JBUI.insets(25, DISTANCE+28, 5, DISTANCE);


        JLabel descriptionLabel = new JLabel(descriptionText);
        descriptionLabel.setForeground(JBColor.GRAY);


        gbc.gridy++; // Move to the next row
        panel.add(checkBox, gbc);
        panel.add(descriptionLabel,gbclabel);// Add description label with constraints
    }

    public CheckboxBuilder(String checkboxText) {
        panel = new JPanel();
        panel.setLayout(new GridBagLayout()); // Use GridBagLayout for more control

        panel.setMaximumSize(new Dimension(1000, 30));



        checkBox = new JCheckBox(checkboxText);

        // Create GridBagConstraints for the checkbox
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Set the column position
        gbc.gridy = 0; // Set the row position
        gbc.anchor = GridBagConstraints.NORTHWEST;// Align to the left
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.insets = JBUI.insets(5, DISTANCE+5, 5, DISTANCE); // Add some padding
        panel.add(checkBox, gbc); // Add checkbox with constraints





        // Create GridBagConstraints for the description label
        gbc.gridy++; // Move to the next row
        panel.add(checkBox, gbc);

    }

        public JPanel getPanel() {
            return panel;
        }

        public JCheckBox getCheckbox() {
            return checkBox;
        }
}
