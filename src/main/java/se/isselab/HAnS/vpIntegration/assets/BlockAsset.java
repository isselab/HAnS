package se.isselab.HAnS.vpIntegration.assets;

import com.intellij.psi.PsiElement;

public class BlockAsset {
    private String name;
    private int version;
    private String assetPath;
    private PsiElement psiElement;

    public BlockAsset(String name,PsiElement psiElement){
        this.name = name;
        this.psiElement = psiElement;
    }
}
