package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelFeatureImpl;
import se.isselab.HAnS.featureModel.psi.impl.FeatureModelPsiImplUtil;
import se.isselab.HAnS.featureView.FeatureViewModel;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationLpqImpl;
import se.isselab.HAnS.folderAnnotation.psi.impl.FolderAnnotationLpqImpl;


import java.util.ArrayList;
import java.util.Map;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        for(FeatureModelFeature elem : FeatureModelUtil.findFeatures(e.getProject())){
            if(elem.getName().equals("UNASSIGNED"))
                break;
            System.out.println("Scanning " + elem.getName() + ":\n");
            for(var child : elem.getChildren()){
                //System.out.println(child.getText() + " " + child.getContainingFile());
            }

            System.out.println("CODE:");
            for (PsiReference reference : ReferencesSearch.search(elem)) {
                System.out.println(reference.getElement().getText() + "  " + reference.getElement().getContainingFile().getName());
                if(reference.getElement().getContainingFile().getName().equals(".feature-to-folder")){
                    printFiles(reference.getElement().getContainingFile().getContainingDirectory(), ".feature-to-folder", "----");
                }
            }

            System.out.println("///////\n");
        }




    }

    void printFiles(PsiDirectory dir, String exclude, String ident){
        for(var file : dir.getFiles()){
            if(file.getName().equals(exclude))
                continue;
            System.out.println(ident + "> " + file.getName());
        }
        for(var subdir : dir.getSubdirectories()){
            System.out.println(ident + "/" + subdir.getName());
            printFiles(subdir, exclude, ident + "----");
        }
    }
}
