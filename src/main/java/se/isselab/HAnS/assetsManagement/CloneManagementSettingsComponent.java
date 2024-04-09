package se.isselab.HAnS.assetsManagement;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;

public class CloneManagementSettingsComponent {
    private JPanel myMainPanel;
    private JCheckBox cloningOption;
    private JCheckBox propagatingOption;
    private JCheckBox showCloneOption;
    public static PropertiesComponent properties = PropertiesComponent.getInstance();
    public static final String ASSETS_MANAGEMENT_PREF_KEY = "AssetsPref";
    private String selected = "none";
    private boolean initialized = false;
    public static CloneManagementSettingsComponent getInstance(){
        return ApplicationManager.getApplication().getService(CloneManagementSettingsComponent.class);
    }

    public JComponent getPanel() {
        myMainPanel = new JPanel(new GridLayout(0, 1));
        cloningOption = new JCheckBox("Enable Cloning Trace Tracking");
        showCloneOption = new JCheckBox("Show Clone Information");
        propagatingOption = new JCheckBox("Notify on Sources Segment Changes");
        myMainPanel.add(cloningOption);
        myMainPanel.add(showCloneOption);
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
        setSelected();
        String current = properties.getValue(ASSETS_MANAGEMENT_PREF_KEY, "none");
        return !selected.equals(current);
    }

    public void apply() {
        setSelected();
        properties.setValue(ASSETS_MANAGEMENT_PREF_KEY, selected);
    }

    public void setSelected(){
        if(cloningOption.isSelected() && propagatingOption.isSelected() && showCloneOption.isSelected()){
            selected = "All";
        }if(cloningOption.isSelected() && !propagatingOption.isSelected()&& !showCloneOption.isSelected()){
            selected = "clone";
        }if(!cloningOption.isSelected() && propagatingOption.isSelected()&& !showCloneOption.isSelected()){
            selected = "propagate";
        }if(!cloningOption.isSelected() && !propagatingOption.isSelected()&& !showCloneOption.isSelected()){
            selected = "none";
        }if(!cloningOption.isSelected() && !propagatingOption.isSelected() && showCloneOption.isSelected()){
            selected = "showClone";
        }if(cloningOption.isSelected() && !propagatingOption.isSelected() && showCloneOption.isSelected()){
            selected = "cloneAndShowClone";
        }if(cloningOption.isSelected() && propagatingOption.isSelected() && !showCloneOption.isSelected()){
            selected = "cloneAndPropagate";
        }if(!cloningOption.isSelected() && propagatingOption.isSelected() && showCloneOption.isSelected()){
            selected = "showCloneAndPropagate";
        }
    }

    public void reset() {
        cloningOption.setSelected(false);
        showCloneOption.setSelected(false);
        propagatingOption.setSelected(false);
    }
    public void init() {
        String current = properties.getValue(ASSETS_MANAGEMENT_PREF_KEY, "none");
        switch(current) {
            case "All":
                cloningOption.setSelected(true);
                showCloneOption.setSelected(true);
                propagatingOption.setSelected(true);
                break;
            case "clone":
                cloningOption.setSelected(true);
                showCloneOption.setSelected(false);
                propagatingOption.setSelected(false);
                break;
            case "showClone":
                cloningOption.setSelected(false);
                showCloneOption.setSelected(true);
                propagatingOption.setSelected(false);
                break;
            case "propagate":
                cloningOption.setSelected(false);
                showCloneOption.setSelected(false);
                propagatingOption.setSelected(true);
                break;
            case "cloneAndShowClone":
                cloningOption.setSelected(true);
                showCloneOption.setSelected(true);
                propagatingOption.setSelected(false);
                break;
            case "cloneAndPropagate":
                cloningOption.setSelected(true);
                showCloneOption.setSelected(false);
                propagatingOption.setSelected(true);
                break;
            case "showCloneAndPropagate":
                cloningOption.setSelected(false);
                showCloneOption.setSelected(true);
                propagatingOption.setSelected(true);
                break;
            default:
                cloningOption.setSelected(false);
                showCloneOption.setSelected(false);
                propagatingOption.setSelected(false);
        }
    }
    public void setAssetsManagementPrefKey(String value) {
        properties.setValue(ASSETS_MANAGEMENT_PREF_KEY, value);
    }
}
