package se.isselab.HAnS.assetsManagement;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class HansAssetsManagementPage implements Configurable {
    private AssetsManagementPreferences assetsManagementPreferences;
    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Assets Management Page";
    }

    @Override
    public @Nullable JComponent createComponent() {
        assetsManagementPreferences = new AssetsManagementPreferences();
        return assetsManagementPreferences.getPanel();
    }

    @Override
    public boolean isModified() {
        return assetsManagementPreferences.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        assetsManagementPreferences.apply();
    }

    @Override
    public void reset() {
        assetsManagementPreferences.reset();
    }

    @Override
    public void disposeUIResources() {
        assetsManagementPreferences = null;
    }

    @Override
    public void cancel() {
        Configurable.super.cancel();
    }
}
