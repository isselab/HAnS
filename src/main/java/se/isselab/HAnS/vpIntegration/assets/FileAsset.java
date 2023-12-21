package se.isselab.HAnS.vpIntegration.assets;

import com.intellij.psi.PsiElement;

public class FileAsset {
    private String name;
    private int version;
    private String assetPath;
    private PsiElement psiElement;

    public FileAsset(String name,PsiElement psiElement){
        this.name = name;
        this.psiElement = psiElement;
    }
}
