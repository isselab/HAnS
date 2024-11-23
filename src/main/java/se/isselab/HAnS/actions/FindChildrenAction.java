package se.isselab.HAnS.actions;

import com.google.api.Usage;
import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageViewManager;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.referencing.FeatureFindUsagesProvider;

import java.util.ArrayList;
import java.util.List;

import static org.mozilla.javascript.ScriptRuntime.typeof;


public class FindChildrenAction extends AnAction {
    public void printAllChildren(FeatureModelFeature feature, String parentFeatureName) {
        List<FeatureModelFeature> children = feature.getFeatureList(); // Get direct children

        if (children.isEmpty()) {
            System.out.println("The Feature " + parentFeatureName + " has no children.");
            return;
        }

        System.out.println("Children of Feature " + parentFeatureName + " are: ");
        for (FeatureModelFeature child : children) {
            String childName = child.getFeatureName();
            System.out.println(childName); // Print the current child
            // Recursive call to get children of the current child
            printAllChildren(child, childName);
        }
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        // try using LangDataKeys.PSI_ELEMENT to get the feature name
        PsiElement langElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (langElement == null) {
            System.out.println("LangDataKeys.PSI_ELEMENT is null.");
        } else if (langElement instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) langElement; // cast to FeatureModelFeature to use getFeatureName()
            String featureName = feature.getFeatureName();
            printAllChildren(feature,featureName);
            }
            /*List<FeatureModelFeature> children = feature.getFeatureList(); // get the children

            int size = children.size();
            int i= 0;
            if (size == 0)
                System.out.println("The Feature "+ featureName + " has no children.");
            else {
                System.out.println("Children of Feature " + featureName + " are: ");

                while (i < size) {
                    System.out.println(children.get(i).getFeatureName()); // print the feature children
                    i++;
                }
            }*/
            /*if (featureName != null && !featureName.isEmpty()) {
                System.out.println("Selected feature name (via LangDataKeys): " + featureName);
            } else {
                System.out.println("Feature name (via LangDataKeys) is empty or null.");
            }
        } else {
            System.out.println("LangDataKeys.PSI_ELEMENT is not a FeatureModelFeature.");
        }*/


    }


}


