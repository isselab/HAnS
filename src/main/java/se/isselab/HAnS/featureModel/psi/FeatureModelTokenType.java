
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
*/package se.isselab.HAnS.featureModel.psi;

import com.intellij.psi.tree.IElementType;
import com.sun.istack.NotNull;
import se.isselab.HAnS.featureModel.FeatureModelLanguage;
import org.jetbrains.annotations.NonNls;

public class FeatureModelTokenType extends IElementType {
    public FeatureModelTokenType(@NotNull @NonNls String debugName) {
        super(debugName, FeatureModelLanguage.INSTANCE);
    }

    @Override
    public String toString() {
        return "FeatureModelTokenType." + super.toString();
    }
}
