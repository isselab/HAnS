package se.ch.HAnS.featureModel.toolWindow;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

public class FeatureView {

    private JButton refreshToolWindowButton;
    private JButton hideToolWindowButton;
    private JPanel content;

    public FeatureView(ToolWindow toolWindow) {
        //hideToolWindowButton.addActionListener(e -> toolWindow.hide(null));
        //refreshToolWindowButton.addActionListener(e -> );
    }

    public JPanel getContent() {
        return content;
    }
}
