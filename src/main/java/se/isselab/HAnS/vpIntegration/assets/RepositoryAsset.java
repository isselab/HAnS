package se.isselab.HAnS.vpIntegration.assets;

import com.intellij.psi.PsiElement;

public class RepositoryAsset {
    private String name;
    private int version;
    private String assetPath;
    private PsiElement psiElement;

    public RepositoryAsset(String name,PsiElement psiElement){
        this.name = name;
        this.psiElement = psiElement;
    }
}
