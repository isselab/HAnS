package se.isselab.HAnS.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

import static org.mozilla.javascript.ScriptRuntime.typeof;


public class FindChildrenAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        // try using LangDataKeys.PSI_ELEMENT to get the feature name
        PsiElement langElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (langElement == null) {
            System.out.println("LangDataKeys.PSI_ELEMENT is null.");
        } else if (langElement instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) langElement; // cast to FeatureModelFeature to use getFeatureName()
            String featureName = feature.getFeatureName();
            PsiElement [] children = feature.getChildren(); // get the children
            int size = children.length;
            int i= 0;
            if (size == 0)
                System.out.println("The Feature "+ featureName + " has no children.");
            else {
                System.out.println("Children of Feature " + featureName + " are: ");

                while (i < size) {
                    System.out.println(children[i].getText()); // print the feature children
                    i++;
                }
            }
            /*if (featureName != null && !featureName.isEmpty()) {
                System.out.println("Selected feature name (via LangDataKeys): " + featureName);
            } else {
                System.out.println("Feature name (via LangDataKeys) is empty or null.");
            }*/
        } else {
            System.out.println("LangDataKeys.PSI_ELEMENT is not a FeatureModelFeature.");
        }


    }


}


