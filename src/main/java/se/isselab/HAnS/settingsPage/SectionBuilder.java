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

import com.intellij.util.ui.*;

import javax.swing.*;
import java.awt.*;

public class SectionBuilder {

    private final JPanel settingsPanel;



    /** Section Builder builds the Default Sections for IntelliJ in the Form of a normal panel **/
    public SectionBuilder(String name) {
        int WIDTH = 10000;
        settingsPanel = new JPanel();
        GridBagLayout layout = new GridBagLayout();
        settingsPanel.setLayout(layout);
        settingsPanel.setOpaque(true);

        JLabel titleLabel = new JLabel(name);
        titleLabel.setOpaque(true);

        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);

        settingsPanel.setMaximumSize(new Dimension(WIDTH, titleLabel.getPreferredSize().height + 5));

        GridBagConstraints c1 = new GridBagConstraints();
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.weighty = 1;
        c1.weightx = 0.0001;
        c1.gridx = 0;
        c1.gridy = 0;
        c1.anchor = GridBagConstraints.NORTHWEST;
        c1.insets = new Insets(0, 0, 0, 5);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.weighty = 1;
        c2.weightx = 1;
        c2.gridx = 1;
        c2.gridy = 0;
        c2.anchor = GridBagConstraints.NORTHWEST;
        c2.insets = JBUI.insets(7, 5, 0, 0);

        settingsPanel.add(titleLabel, c1);
        settingsPanel.add(separator, c2);
    }

    public JPanel getSettingsPanel() {
        return settingsPanel;
    }
}
