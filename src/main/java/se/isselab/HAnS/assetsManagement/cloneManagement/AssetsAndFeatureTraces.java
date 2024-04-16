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

import com.intellij.psi.*;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsComponent;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;

import java.util.ArrayList;

public class AssetsAndFeatureTraces {
    public static PsiMethod clonedMethod;
    public static PsiClass clonedClass;
    public static String sourceProjectName;

    public static String sourcePath;
    public static ArrayList<String> subFeatureTrace;
    public static ArrayList<PsiElement> featuresAnnotations;

    public static void resetAssetClones() {
        clonedClass = null;
        clonedMethod = null;
        sourceProjectName = null;
        sourcePath = null;
    }
    public static void resetTraces() {
        subFeatureTrace = null;
        featuresAnnotations = null;
        FeaturesCodeAnnotations.getInstance().clearFeatures();
    }
    public static boolean isAllPreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("All");
    }
    public static boolean isClonePreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("clone");
    }
    public static boolean isPropagatePreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("propagate");    }
    public static boolean isShowClonePreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("showClone");    }
    public static boolean isCloneAndPropagatePreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("cloneAndPropagate");    }
    public static boolean isCloneAndShowClonePreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("cloneAndShowClone");    }
    public static boolean isShowCloneAndPropagatePreference(){
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        return settingsState.prefKey.equals("showCloneAndPropagate");    }
}
