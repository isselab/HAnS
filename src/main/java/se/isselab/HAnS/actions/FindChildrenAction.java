package se.isselab.HAnS.actions;

import com.intellij.lang.findUsages.FindUsagesProvider;
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
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.usages.*;
import org.jetbrains.annotations.NotNull;
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
        FindUsagesProvider findUsagesProvider = new FeatureFindUsagesProvider();
        // try using LangDataKeys.PSI_ELEMENT to get the feature name
        PsiElement langElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);

        if (langElement == null) {
            System.out.println("LangDataKeys.PSI_ELEMENT is null.");
        }
        FeatureModelFeature feature = (FeatureModelFeature) langElement; // cast to FeatureModelFeature to use getFeatureName()

        String featureName = feature.getFeatureName();

        //printAllChildren(feature,featureName);

        List<PsiElement> child = buildFeatureHierarchy(anActionEvent);

        int size = child.size();
        /*Project project = anActionEvent.getProject();
        if (project == null) {
            System.out.println("Project is null.");
            return;
        }*/
        /*
        int i= 0;
        if (size == 0)
            System.out.println("The Feature "+ featureName + " has no children.");
        else {
            System.out.println("Children of Feature " + featureName + " are: ");
            while (i < size) {
                Project project1 = child.get(i).getProject();
                Usage[] usages1 = findUsagesInProject(child.get(i)); // print the feature children
                showFindUsages(project1, usages1);
                i++;
            }
        }
*/
        // get the project
        Project project = anActionEvent.getProject();
        if (project == null) {
            System.out.println("Project is null.");
            return;
        }
        // create a list to save the usages
        List<Usage> allUsages = new ArrayList<>();
        for (PsiElement element : child) {
            Usage[] elementUsages = findUsagesInProject(element);
            allUsages.addAll(Arrays.asList(elementUsages));
        }

        // Display all usages in one tab
        if (allUsages.isEmpty()) {
            System.out.println("No usages found for Feature " + featureName + " and its children.");
        } else {
            showFindUsages(project, allUsages.toArray(new Usage[0]));
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

    private void showFindUsages(Project project, Usage[] usages) {
        UsageViewPresentation presentation = new UsageViewPresentation();
        presentation.setTabText("Find Usages");
        presentation.setScopeText("Project Scope");
        presentation.setCodeUsages(true);
        presentation.setOpenInNewTab(true);

        UsageViewManager usageViewManager = UsageViewManager.getInstance(project);
        usageViewManager.showUsages(new UsageTarget[]{}, usages, presentation);
    }
    /*private void collectHierarchy(PsiElement element, List<PsiElement> hierarchy) {
        if (element == null) return;

        hierarchy.add(element); // Add the current element

        PsiElement[] children = element.getChildren(); // Get direct children
        for (PsiElement child : children) {
            collectHierarchy(child, hierarchy); // Recursive call for each child
        }
    }*/


    private Usage[] findUsagesInProject(PsiElement element) {
        Project project = element.getProject(); // Find usages
        Collection<PsiReference> references = ReferencesSearch.search(element, GlobalSearchScope.projectScope(project)).findAll();
        // Convert PsiReference to Usage
        List<UsageInfo2UsageAdapter> usageList = new ArrayList<>();
        for (PsiReference reference : references) {
            PsiElement usageElement = reference.getElement();
            if (usageElement != null) {
                usageList.add(new UsageInfo2UsageAdapter(new UsageInfo(usageElement))); }
        }
        return usageList.toArray(new Usage[0]);
    }
/*
    private void showFindUsages(Project project, Usage[] usages) {
        UsageViewPresentation presentation = new UsageViewPresentation();
        presentation.setTabText("Find Usages");
        presentation.setScopeText("Project Scope");
        UsageViewManager usageViewManager = UsageViewManager.getInstance(project);
        UsageView usageView = usageViewManager.showUsages(new UsageTarget[]{}, usages, presentation);
        presentation.setCodeUsages(true);
        presentation.setOpenInNewTab(true);
    }*/

}


