package se.isselab.HAnS.actions;

import com.intellij.find.FindManager;
import com.intellij.find.FindModel;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesManager;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.usageView.UsageInfo;
import com.intellij.usages.*;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.referencing.FeatureFindUsagesProvider;

import java.util.*;

import static org.mozilla.javascript.ScriptRuntime.typeof;


public class FindChildrenAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        FindUsagesProvider findUsagesProvider = new FeatureFindUsagesProvider();
        // try using LangDataKeys.PSI_ELEMENT to get the feature name
        PsiElement langElement = anActionEvent.getData(LangDataKeys.PSI_ELEMENT);
        if (langElement == null) {
            System.out.println("LangDataKeys.PSI_ELEMENT is null.");
        } else if (langElement instanceof FeatureModelFeature) {
            FeatureModelFeature feature = (FeatureModelFeature) langElement; // cast to FeatureModelFeature to use getFeatureName()
            String featureName = feature.getFeatureName();
            List<PsiElement> child = buildFeatureHierarchy(anActionEvent);
            findUsagesProvider.getWordsScanner();
            PsiElement [] children = feature.getChildren(); // get the children
            int size = children.length;
            int i= 0;
            if (size == 0)
                System.out.println("The Feature "+ featureName + " has no children.");
            else {
                System.out.println("Children of Feature " + featureName + " are: ");
                while (i < size) {

                    // TODO: not showing Find Usages results correctly - need to fix

                    // TODO: understand the code with findUsages

                    Project project = child.get(i).getProject();
                    Usage[] usages = findUsagesInProject(child.get(i)); // print the feature children
                    showFindUsages(project, usages);
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


    public List<PsiElement> buildFeatureHierarchy(@NotNull AnActionEvent event) {
        PsiElement parentFeature = event.getData(LangDataKeys.PSI_ELEMENT);
        if (parentFeature == null) return Collections.emptyList();

        // Collect parent and children
        List<PsiElement> hierarchy = new ArrayList<>();
        hierarchy.add(parentFeature); // Add parent
        hierarchy.addAll(Arrays.asList(parentFeature.getChildren())); // Add children

        return hierarchy;
    }

    /*
    private void findUsagesForElement(PsiElement element, AnActionEvent event) {
        // not saving the previous entries

        // Create a UsageViewPresentation for displaying results
        UsageViewPresentation presentation = new UsageViewPresentation();
        presentation.setCodeUsages(true);
        presentation.setTabName("Find Usages for " + element.getText());
        presentation.setTabText("Find Usages");

        // Use UsageViewManager to handle displaying usages
        UsageViewManager usageViewManager = UsageViewManager.getInstance(event.getProject());
        if (usageViewManager == null) {
            System.out.println("UsageViewManager is unavailable.");
            return;
        }

        // Collect usages for the element
        Usage[] usages = findUsagesInProject(element);

        // Provide an empty UsageTarget array since we don't have specific targets
        UsageTarget[] usageTargets = UsageTarget.EMPTY_ARRAY;

        // Display usages in a usage view
        usageViewManager.showUsages(usageTargets, usages, presentation);
    }
     */

    private Usage[] findUsagesInProject(PsiElement element) {
        Project project = element.getProject(); // Find usages
        Collection<PsiReference> references = ReferencesSearch.search(element, GlobalSearchScope.projectScope(project)).findAll();
        // Convert PsiReference to Usage
        List<Usage> usageList = new ArrayList<>();
        for (PsiReference reference : references) {
            PsiElement usageElement = reference.getElement();
            if (usageElement != null) {
                usageList.add(new UsageInfo2UsageAdapter(new UsageInfo(usageElement))); }
        }
        return usageList.toArray(new Usage[0]);
    }

    private void showFindUsages(Project project, Usage[] usages) {
        UsageViewPresentation presentation = new UsageViewPresentation();
        presentation.setTabText("Find Usages");
        presentation.setScopeText("Project Scope");
        UsageViewManager usageViewManager = UsageViewManager.getInstance(project);
        UsageView usageView = usageViewManager.showUsages(new UsageTarget[]{}, usages, presentation);
        // Optionally, you can add more configurations to the presentation
        presentation.setCodeUsages(true);
        presentation.setOpenInNewTab(true);
    }

    /*
    private Usage[] findUsagesInProject(PsiElement element) {
        Project project = element.getProject();

        FindUsagesManager findUsagesManager = FindUsagesManager.getInstance(project);
        FindUsagesHandler handler = findUsagesManager.getFindUsagesHandler(element, true);
        if (handler == null) {
            return new Usage[0];
        }

        FindUsagesOptions options = handler.getFindUsagesOptions();
        options.isSearchForTextOccurrences = true;

        List<Usage> usageList = new ArrayList<>();
        handler.processElementUsages(element, info -> {
            PsiElement usageElement = info.getElement();
            if (usageElement != null) {
                usageList.add(new UsageInfo2UsageAdapter(info));
            }
            return true;
        }, options);

        return usageList.toArray(new Usage[0]);
    }



    private Usage[] findUsagesInProject(PsiElement element) {
        Project project = element.getProject();

        FindModel findModel = new FindModel();
        findModel.setCaseSensitive(true);
        findModel.setStringToFind(element.getText());

        FindManager findManager = FindManager.getInstance(project); // Set the search scope using FindManager
        GlobalSearchScope searchScope = GlobalSearchScope.projectScope(project);
        FindUsagesOptions findUsagesOptions = new FindUsagesOptions(searchScope);

        // Find usages
        UsageInfo[] usageInfos = findManager.findUsages(element, findModel, findUsagesOptions);

        // Convert UsageInfo[] to Usage[]
        List<Usage> usageList = new ArrayList<>();
        for (UsageInfo usageInfo : usageInfos) {
            PsiElement usageElement = usageInfo.getElement();
            if (usageElement != null) {
                usageList.add(new UsageInfo2UsageAdapter(usageInfo));
            }
        }

        return usageList.toArray(new Usage[0]);
    }
    */
}


