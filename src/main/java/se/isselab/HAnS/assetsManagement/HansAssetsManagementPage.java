package se.isselab.HAnS.assetsManagement;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HansAssetsManagementPage implements Configurable {
    private CloneManagementSettingsComponent cloneManagementSettingsComponent;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Clone Management Page";
    }

    @Override
    public @Nullable JComponent createComponent() {
        cloneManagementSettingsComponent = new CloneManagementSettingsComponent();
        return cloneManagementSettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        return cloneManagementSettingsComponent.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        cloneManagementSettingsComponent.apply();
    }

    @Override
    public void reset() {
        cloneManagementSettingsComponent.reset();
    }

    @Override
    public void disposeUIResources() {
        cloneManagementSettingsComponent = null;
    }

    @Override
    public void cancel() {
        Configurable.super.cancel();
    }

    public CloneManagementSettingsComponent getCloneManagementSettingsComponent() {
        return this.cloneManagementSettingsComponent;
    }
}
