package se.isselab.HAnS.actions;

import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Factory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.UsageInfo2UsageAdapter;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageViewManager;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.usages.*;
import com.intellij.usages.rules.UsageGroupingRule;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureChildren.FeatureUsageGroup;
import se.isselab.HAnS.featureChildren.FeatureUsageGroupingRule;
import se.isselab.HAnS.featureChildren.FeatureUsageGroupingRuleProvider;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.referencing.FeatureFindUsagesProvider;

import java.util.ArrayList;
import java.util.List;

import java.util.*;


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
        // try using LangDataKeys.PSI_ELEMENT to get the feature
        PsiElement langElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);

        // no feature selected
        if (langElement == null) {
            System.out.println("LangDataKeys.PSI_ELEMENT is null.");
        }
        FeatureModelFeature feature = (FeatureModelFeature) langElement; // cast to FeatureModelFeature to use getFeatureName()

        String featureName = feature.getFeatureName();

        // parent feature and the children feature should be stored in the hiearchy
        List<PsiElement> child = buildFeatureHierarchy(anActionEvent);

        int size = child.size();

        // get the project
        Project project = anActionEvent.getProject();
        if (project == null) {
            System.out.println("Project is null.");
            return;
        }

        //
        Map<PsiElement, List<Usage>> usages = collectFeatureUsages(project, child);
        if (usages.isEmpty()) {
            System.out.println("No usages found.");
        } else {
            showUsagesWithGrouping(project, usages);
        }

    }

    // create hierarchy list of the given feature and all its children, acts as a public interface
    public List<PsiElement> buildFeatureHierarchy(@NotNull AnActionEvent event) {
        PsiElement parentFeature = event.getData(LangDataKeys.PSI_ELEMENT);
        if (parentFeature == null) return Collections.emptyList();

        // Start with the parent and collect its hierarchy recursively
        List<PsiElement> hierarchy = new ArrayList<>();
        collectHierarchy(parentFeature, hierarchy);
        return hierarchy;
    }
    // recursive method that does the actual work of traversing and collecting elements
    private void collectHierarchy(PsiElement element, List<PsiElement> hierarchy) {
        if (element == null) return;

        hierarchy.add(element); // Add the current element

        PsiElement[] children = element.getChildren(); // Get direct children
        for (PsiElement child : children) {
            collectHierarchy(child, hierarchy); // Recursive call for each child
        }
    }

    private Map<PsiElement, List<Usage>> collectFeatureUsages(Project project, List<PsiElement> featureElements) {
        Map<PsiElement, List<Usage>> featureUsagesMap = new HashMap<>();

        for (PsiElement feature : featureElements) {
            List<Usage> usages = new ArrayList<>();
            // Collect usages for the feature
            ReferencesSearch.search(feature).forEach(reference -> {
                Usage usage = new UsageInfo2UsageAdapter(new UsageInfo(reference));
                usages.add(usage);
            });

            featureUsagesMap.put(feature, usages);
        }

        return featureUsagesMap;
    }

    // TODO: not working as we wanted, each feature should be shown seperately, maybe need to adjust the Groupingrules?
    private void showUsagesWithGrouping(Project project, Map<PsiElement, List<Usage>> usages) {
        // Prepare the usage presentation
        UsageViewPresentation presentation = new UsageViewPresentation();
        presentation.setTabText("Feature Usages");
        presentation.setScopeText("Project Scope");
        presentation.setCodeUsages(true);
        presentation.setOpenInNewTab(true);

        // Flatten all usages into a single list
        List<Usage> allUsages = new ArrayList<>();
        Map<Usage, UsageGroup> usageGroupMap = new HashMap<>();

        // Create the mapping of usages to groups
        for (Map.Entry<PsiElement, List<Usage>> entry : usages.entrySet()) {
            PsiElement feature = entry.getKey();
            List<Usage> featureUsages = entry.getValue();

            // Use the feature's text (or another attribute) for grouping
            String groupName = feature.getText(); // Adjust if needed
            FeatureUsageGroup group = new FeatureUsageGroup(feature, groupName);

            // Add each usage and map it to the group
            for (Usage usage : featureUsages) {
                allUsages.add(usage);
                usageGroupMap.put(usage, group);
            }
        }

        // Create the grouping rule provider
        FeatureUsageGroupingRuleProvider groupingRuleProvider = new FeatureUsageGroupingRuleProvider(usageGroupMap);

        // Show usages in the Usage View
        UsageViewManager.getInstance(project).showUsages(
                new UsageTarget[]{},                      // No specific usage target
                allUsages.toArray(new Usage[0]),          // All usages
                presentation                              // Presentation settings
        );
    }



}

