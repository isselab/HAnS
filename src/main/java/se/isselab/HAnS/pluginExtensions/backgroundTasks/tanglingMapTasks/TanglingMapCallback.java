/*
Copyright 2024 Johan Martinson

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

package se.isselab.HAnS.pluginExtensions.backgroundTasks.tanglingMapTasks;

import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.HashSet;
import java.util.Map;

/**
 * Callback for the TanglingMap tasks.
 * All methods are optional to implement, and do nothing if not implemented.
 */
public interface TanglingMapCallback {
    default void onComplete(HashSet<FeatureModelFeature> tangledFeatures){}

    default void onComplete(Map<FeatureModelFeature, HashSet<FeatureModelFeature>> tanglingMap){}
}
