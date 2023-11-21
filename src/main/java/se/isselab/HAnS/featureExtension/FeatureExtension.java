package se.isselab.HAnS.featureExtension;

import com.intellij.psi.PsiFile;
import se.isselab.HAnS.featureView.FeatureViewFactory;

public abstract class FeatureExtension {
    public PsiFile getFeatureModel () {
        return FeatureViewFactory.getFeatureModel();
    }
}
