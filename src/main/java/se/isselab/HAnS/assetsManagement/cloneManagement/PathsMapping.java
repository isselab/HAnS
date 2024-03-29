package se.isselab.HAnS.assetsManagement.cloneManagement;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
@State(
        name = "se.isselab.HAnS.assetsManagement.cloningManagement.PathsMapping",
        storages = @Storage("plugin.xml")
)
public class PathsMapping implements PersistentStateComponent<PathsMapping> {
    public Map<String, String> paths = new HashMap<>();
    @Override
    public @Nullable PathsMapping getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PathsMapping pathsMapping) {
        XmlSerializerUtil.copyBean(pathsMapping, this);
    }

    @Override
    public void noStateLoaded() {
        PersistentStateComponent.super.noStateLoaded();
    }

    public static PathsMapping getInstance(){
        return ApplicationManager.getApplication().getService(PathsMapping.class);
    }
}
