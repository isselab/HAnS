package se.isselab.HAnS.assetsManagement;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HansAssetsManagementPage implements Configurable {
    private AssetsManagementSettings assetsManagementSettings;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Assets Management Page";
    }

    @Override
    public @Nullable JComponent createComponent() {
        assetsManagementSettings = new AssetsManagementSettings();
        return assetsManagementSettings.getPanel();
    }

    @Override
    public boolean isModified() {
        return assetsManagementSettings.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        assetsManagementSettings.apply();
    }

    @Override
    public void reset() {
        assetsManagementSettings.reset();
    }

    @Override
    public void disposeUIResources() {
        assetsManagementSettings = null;
    }

    @Override
    public void cancel() {
        Configurable.super.cancel();
    }
}
