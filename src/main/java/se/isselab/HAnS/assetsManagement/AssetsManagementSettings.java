package se.isselab.HAnS.assetsManagement;

import com.intellij.ide.util.PropertiesComponent;

import javax.swing.*;
import java.awt.*;

public class AssetsManagementSettings {
    private JPanel myMainPanel;
    private JCheckBox cloningOption;
    private JCheckBox propagatingOption;
    private static PropertiesComponent properties = PropertiesComponent.getInstance();
    private static final String ASSETS_MANAGEMENT_PREF_KEY = "AssetsPref";



    public JComponent getPanel() {
        myMainPanel = new JPanel(new GridLayout(0, 1));
        cloningOption = new JCheckBox("Enable Cloning Tracing");
        propagatingOption = new JCheckBox("Enable Propagating Assets");
        myMainPanel.add(cloningOption);
        myMainPanel.add(propagatingOption);
        properties = PropertiesComponent.getInstance();
        return myMainPanel;
    }

    public boolean isModified() {
        // Implement logic to check if settings are modified
        String selected = "none";
        if(cloningOption.isSelected() && propagatingOption.isSelected()){
            selected = "both";
        }if(cloningOption.isSelected() && !propagatingOption.isSelected()){
            selected = "clone";
        }if(cloningOption.isSelected() && propagatingOption.isSelected()){
            selected = "propagate";
        }
        String current = properties.getValue(ASSETS_MANAGEMENT_PREF_KEY, "none");
        return !selected.equals(current);
    }

    public void apply() {
        // Implement logic to apply settings changes
    }

    public void reset() {
        // Implement logic to reset settings to their defaults
    }
}
