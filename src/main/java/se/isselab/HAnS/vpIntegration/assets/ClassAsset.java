package se.isselab.HAnS.vpIntegration.assets;

import com.intellij.psi.PsiElement;

public class ClassAsset {
    private String name;
    private int version;
    private String assetPath;
    private PsiElement psiElement;

    public ClassAsset(String name,PsiElement psiElement){
        this.name = name;
        this.psiElement = psiElement;
    }
}
