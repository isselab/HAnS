/*
Copyright 2021 Herman Jansson & Johan Martinson

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
package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

public class DeleteAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        if (e.getData(LangDataKeys.PSI_ELEMENT) instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) e.getData(LangDataKeys.PSI_ELEMENT);
            if (feature != null) {
                feature.deleteFeature();
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        var array = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if(array != null) {
            e.getPresentation().setEnabled(array.length == 1);
        }
    }
}
