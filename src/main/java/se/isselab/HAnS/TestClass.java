package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.fileAnnotation.psi.FileAnnotationFeatureName;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFeatureNameImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationFileNameImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationLpqImpl;
import se.isselab.HAnS.fileAnnotation.psi.impl.FileAnnotationPsiImplUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        Map<String, ArrayList<FeatureLocationInfo>> map = FeatureLocationInfo.getAllFeatureLocations(e.getProject());

        for(String value : map.keySet()){
            System.out.println("/////////");
            System.out.println(value);
            for(FeatureLocationInfo info : map.get(value)){
                System.out.print("   ");
                info.printMembers();
            }
            System.out.println("/////////\n");
        }


    }
}
