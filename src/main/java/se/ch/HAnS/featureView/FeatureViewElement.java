package se.ch.HAnS.featureView;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.SortableTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFeature;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

import java.util.ArrayList;
import java.util.List;

public class FeatureViewElement implements StructureViewTreeElement, SortableTreeElement {

    private final FeatureModelFeatureImpl myElement;

    public FeatureViewElement(FeatureModelFeatureImpl element) {
        this.myElement = element;
    }

    @Override
    public Object getValue() {
        return myElement;
    }

    @Override
    public void navigate(boolean requestFocus) {
        myElement.navigate(requestFocus);
    }

    @Override
    public boolean canNavigate() {
        return myElement.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        return myElement.canNavigateToSource();
    }

    @NotNull
    @Override
    public String getAlphaSortKey() {
        String name = myElement.getName();
        return name != null ? name : "";
    }

    @NotNull
    @Override
    public ItemPresentation getPresentation() {
        ItemPresentation presentation = myElement.getPresentation();
        return presentation != null ? presentation : new PresentationData();
    }

    @NotNull
    @Override
    public TreeElement @NotNull [] getChildren() {
        myElement.getFeatureList();
        if (!myElement.getFeatureList().isEmpty()) {
            List<TreeElement> treeElements = new ArrayList<>();
            for (FeatureModelFeature feature : myElement.getFeatureList()) {
                treeElements.add(new FeatureViewElement((FeatureModelFeatureImpl) feature));
            }
            return treeElements.toArray(new TreeElement[0]);
        }
        return EMPTY_ARRAY;
    }

}