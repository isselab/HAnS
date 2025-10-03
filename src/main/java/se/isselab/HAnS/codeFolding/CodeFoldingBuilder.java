package se.isselab.HAnS.codeFolding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureLocation.FeatureLocationManager;

import java.util.*;

public class CodeFoldingBuilder extends FoldingBuilderEx {

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        List<FoldingDescriptor> descriptors = new ArrayList<>();

        // Get the PsiFile for the current root element
        PsiFile file = root.getContainingFile();
        if (file == null) {
            return descriptors.toArray(new FoldingDescriptor[0]);
        }
        HashMap<String, FeatureFileMapping> allFeatureFileMappings = FeatureLocationManager.getAllFeatureFileMappings(root.getProject());

        root.accept(new PsiRecursiveElementVisitor(true) {
            @Override
            public void visitComment(@NotNull PsiComment element) {

                for (String featureID : allFeatureFileMappings.keySet()) {
                    FeatureFileMapping featureFileMapping = allFeatureFileMappings.get(featureID);
                    for (FeatureLocation featureLocation : featureFileMapping.getFeatureLocations()) {
                        if (!Objects.equals(featureLocation.getMappedBy(), "/" + file.getName())) continue;
                        for (FeatureLocationBlock featureLocationBlock : featureLocation.getFeatureLocations()) {

                            TextRange range = new TextRange(
                                    document.getLineStartOffset(featureLocationBlock.getStartLine()),
                                    document.getLineEndOffset(featureLocationBlock.getEndLine())
                            );

                            descriptors.add(new FoldingDescriptor(
                                    element.getNode(),
                                    range,
                                    null,
                                    "[Feature: " + featureID + "]"
                            ));
                        }
                    }
                }
                super.visitComment(element);
                }
        });
        return descriptors.toArray(FoldingDescriptor.EMPTY_ARRAY);
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
