package se.isselab.HAnS;

import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureExtension.FeatureService;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.List;

public class TestClass extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        FeatureService featureService = e.getProject().getService(FeatureService.class);
        var fileMappings = featureService.getAllFeatureFileMappings();

        FeatureModelFeature feature = FeatureModelUtil.findFeatures(e.getProject()).get(3);


        NavigationUtil.openFileWithPsiElement(feature, false, false);

        //before
        /*for(var featureLPQ : fileMappings.keySet()){
            System.out.println(featureLPQ);
            var fileMapping = fileMappings.get(featureLPQ);
            for(var path : fileMapping.getAllFeatureLocations().keySet()){
                System.out.println("  Scanning: " + path);
                for(var location : fileMapping.getAllFeatureLocations().get(path).second){
                    System.out.println("    Location: " + location.toString());
                }
                System.out.println("    Lines: " + fileMapping.getFeatureLineCountInFile(path));
            }
            System.out.println("Total lines: " + fileMapping.getTotalFeatureLineCount());
        }*/

        //now
        for(var featureLPQ : fileMappings.keySet()){
            var fileMapping = fileMappings.get(featureLPQ);
            for(var location : fileMapping.getFeatureLocations()){
                System.out.println(" scanning: " + location.getMappedFeature() + "  " + location.getMappedPath() + " as " + location.getAnnotationType());
                for(var block : location.getFeatureLocations()){
                    System.out.println(block.toString());
                }

            }
        }

    }
}
