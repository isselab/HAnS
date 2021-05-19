package se.ch.HAnS.featureView;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.FeatureModelFile;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

public class FeatureViewModel extends StructureViewModelBase implements
        StructureViewModel.ElementInfoProvider {

    public FeatureViewModel(PsiFile psiFile) {
        super(psiFile, new FeatureViewElement((FeatureModelFeatureImpl) psiFile.getFirstChild()));
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return element instanceof FeatureModelFile;
    }

}