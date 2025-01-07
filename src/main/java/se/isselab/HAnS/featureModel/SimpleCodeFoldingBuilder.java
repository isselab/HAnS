package se.isselab.HAnS.featureModel;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

public class SimpleCodeFoldingBuilder extends FoldingBuilderEx {

    @Override
    public @NotNull FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement root,
                                                         @NotNull Document document,
                                                         boolean quick) {
        notify("Building fold regions...", NotificationType.INFORMATION, root.getProject());

        for (PsiElement child : root.getChildren()) {
            notify("Child: " + child.toString(), NotificationType.INFORMATION, root.getProject());
            if (child instanceof FeatureModelFeature) {
                notify("FeatureModelFeature found. Adding folding region.", NotificationType.INFORMATION, root.getProject());
                return new FoldingDescriptor[] {
                        new FoldingDescriptor(child.getNode(), child.getTextRange())
                };
            }
        }

        notify("No FeatureModelFeature found. Returning empty array.", NotificationType.WARNING, root.getProject());
        return FoldingDescriptor.EMPTY_ARRAY;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        notify("Returning placeholder text for node: " + node.toString(), NotificationType.INFORMATION, null);
        return "..."; // Placeholder
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        notify("Node collapsed by default: " + node.toString(), NotificationType.INFORMATION, null);
        return true; // Collapsed by default
    }

    private void notify(String content, NotificationType type, Project project) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Custom Code Folding")
                .createNotification(content, type)
                .notify(project);
    }
}
