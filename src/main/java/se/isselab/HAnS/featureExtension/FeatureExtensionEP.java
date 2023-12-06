package se.isselab.HAnS.featureExtension;



import com.intellij.psi.PsiFile;
import com.intellij.serviceContainer.LazyExtensionInstance;
import com.intellij.util.xmlb.annotations.Attribute;

import org.jetbrains.annotations.Nullable;

public final class FeatureExtensionEP extends LazyExtensionInstance<FeatureExtension> {

    @Attribute("implementation")
    public String implementation;

    @Attribute("feature model")
    public PsiFile featureModel;


    /*public void useExtension() {
        FeatureExtensionBean[] extensionBean = EXTENSION_POINT_NAME.getExtensions();
        PsiFile featuremodel = extensionBean[0].findFeatureModel();
        System.out.println(featuremodel.getText());
    }*/

    @Override
    protected @Nullable String getImplementationClassName() {
        return implementation;
    }



}
