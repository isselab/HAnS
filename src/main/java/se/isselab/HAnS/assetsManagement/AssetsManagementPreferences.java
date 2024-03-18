package se.isselab.HAnS.assetsManagement;

import com.intellij.ide.util.PropertiesComponent;

import javax.swing.*;
import java.awt.*;

public class AssetsManagementPreferences {
    private JPanel myMainPanel;
    private JCheckBox cloningOption;
    private JCheckBox propagatingOption;
    public static PropertiesComponent properties = PropertiesComponent.getInstance();
    public static final String ASSETS_MANAGEMENT_PREF_KEY = "AssetsPref";
    private String selected = "none";
    private boolean initialized = false;



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
        if(!initialized){
            init();
            initialized = true;
            return false;
        }
        selected = "none";
        if(cloningOption.isSelected() && propagatingOption.isSelected()){
            selected = "both";
        }if(cloningOption.isSelected() && !propagatingOption.isSelected()){
            selected = "clone";
        }if(!cloningOption.isSelected() && propagatingOption.isSelected()){
            selected = "propagate";
        }
        String current = properties.getValue(ASSETS_MANAGEMENT_PREF_KEY, "none");
        return !selected.equals(current);
    }

    public void apply() {
        if(cloningOption.isSelected() && propagatingOption.isSelected()){
            selected = "both";
        }if(cloningOption.isSelected() && !propagatingOption.isSelected()){
            selected = "clone";
        }if(!cloningOption.isSelected() && propagatingOption.isSelected()){
            selected = "propagate";
        }if(!cloningOption.isSelected() && !propagatingOption.isSelected()){
            selected = "none";

        }
        properties.setValue(ASSETS_MANAGEMENT_PREF_KEY, selected);
    }

    public void reset() {
        cloningOption.setSelected(false);
        propagatingOption.setSelected(false);
    }
    public void init() {
        String current = properties.getValue(ASSETS_MANAGEMENT_PREF_KEY, "none");
        switch(current) {
            case "both":
                cloningOption.setSelected(true);
                propagatingOption.setSelected(true);
                break;
            case "clone":
                cloningOption.setSelected(true);
                propagatingOption.setSelected(false);
                break;
            case "propagate":
                cloningOption.setSelected(false);
                propagatingOption.setSelected(true);
                break;
            default:
                cloningOption.setSelected(false);
                propagatingOption.setSelected(false);
        }
        System.out.println(cloningOption.isSelected());
        System.out.println(propagatingOption.isSelected());
    }
}
