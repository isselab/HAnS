/*
Copyright 2024 Ahmad Al Shihabi

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
