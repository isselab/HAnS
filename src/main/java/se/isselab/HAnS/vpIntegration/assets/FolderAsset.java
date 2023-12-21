package se.isselab.HAnS.vpIntegration.assets;

import com.intellij.psi.PsiElement;

public class FolderAsset {
    private String name;
    private int version;
    private String assetPath;
    private PsiElement psiElement;

    public FolderAsset(String name,PsiElement psiElement){
        this.name = name;
        this.psiElement = psiElement;
    }
}
