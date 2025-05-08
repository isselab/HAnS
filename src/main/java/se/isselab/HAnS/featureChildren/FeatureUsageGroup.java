package se.isselab.HAnS.featureChildren;

import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.platform.backend.navigation.NavigationRequest;
import com.intellij.psi.PsiElement;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.usages.UsageGroup;
import com.intellij.openapi.project.Project;
import javax.swing.*;

import com.intellij.usages.UsageView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FeatureUsageGroup implements UsageGroup {
    private final PsiElement featureElement;
    private final String featureName;

    // Constructor to initialize with a feature and its name
    public FeatureUsageGroup(@NotNull PsiElement featureElement, @NotNull String featureName) {
        this.featureElement = featureElement;
        this.featureName = featureName;
    }

    // Display name of the group
    public @NotNull String getText() {
        return featureName;
    }

    @Override
    public @Nullable Icon getIcon() {
        return UsageGroup.super.getIcon();
    }

    @Override
    public @NlsContexts.ListItem @NotNull String getPresentableGroupText() {
        return UsageGroup.super.getPresentableGroupText();
    }

    @Override
    public @Nullable FileStatus getFileStatus() {
        return UsageGroup.super.getFileStatus();
    }

    @Override
    public @Nullable NavigationRequest navigationRequest() {
        return UsageGroup.super.navigationRequest();
    }

    // Allows navigation to the group (e.g., to the feature declaration)
    @Override
    public void navigate(boolean focus) {
        if (canNavigate() && featureElement instanceof com.intellij.pom.Navigatable) {
            ((com.intellij.pom.Navigatable) featureElement).navigate(focus);
        }
    }


    // Checks if navigation to the feature is possible
    @Override
    public boolean canNavigate() {
        return featureElement != null && featureElement.isValid();
    }

    // Checks if navigation to the source of the feature is possible
    @Override
    public boolean canNavigateToSource() {
        return canNavigate();
    }

    // Checks if the group is still valid
    @Override
    public boolean isValid() {
        return featureElement != null && featureElement.isValid();
    }

    @Override
    public SimpleTextAttributes getTextAttributes(boolean isSelected) {
        return UsageGroup.super.getTextAttributes(isSelected);
    }

    @Override
    public void update() {
        UsageGroup.super.update();
    }

    @Override
    public int compareTo(@NotNull UsageGroup o) {
        return 0;
    }
}
