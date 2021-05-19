package se.ch.HAnS.structure;

import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.ch.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;

public class FeatureViewModel extends StructureViewModelBase {

    public FeatureViewModel(PsiFile psiFile) {
        super(psiFile, new FeatureViewElement((FeatureModelFeatureImpl) psiFile.getFirstChild()));
    }

    public Sorter @NotNull [] getSorters() {
        return new Sorter[]{Sorter.ALPHA_SORTER};
    }

}