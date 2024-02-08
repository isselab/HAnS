package se.isselab.HAnS;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureModel.FeatureModelUtil;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;
import se.isselab.HAnS.metrics.FeatureDepths;
import se.isselab.HAnS.metrics.ProjectStructureTree;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.intellij.psi.search.FilenameIndex.getAllFilesByExt;
import static com.intellij.psi.search.FilenameIndex.getVirtualFilesByName;

public class TestDepthMetrics extends AnAction {

    private String getFeatureModelPath() {
        Project project = ProjectManager.getInstance().getOpenProjects()[0];
        return project.getBasePath();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println(getFeatureModelPath());
        int depth = 0;
        ProjectStructureTree projectTree = ProjectStructureTree.buildTree(e.getProject());
        ProjectStructureTree.printTree(projectTree, "-");

        List<FeatureModelFeature> features = FeatureModelUtil.findFeatures(e.getProject());
        String featureExampleLPQ = features.get((int)(Math.random() * features.size()) + 1).getLPQText();
        System.out.println("Random Lpq " + featureExampleLPQ);
        System.out.println(FeatureDepths.getAvgNestingDepth(projectTree, featureExampleLPQ));
        System.out.println(FeatureDepths.getMaxNestingDepth(projectTree, featureExampleLPQ));
        System.out.println(FeatureDepths.getMinNestingDepth(projectTree, featureExampleLPQ));
        System.out.println(FeatureDepths.getNumberOfAnnotatedFiles(projectTree, featureExampleLPQ));

        List<Path> allFolders;
        try {
            allFolders = Files.walk(Paths.get(getFeatureModelPath()), 1)
                    .filter(Files::isDirectory)
                    .toList();
            int randomFolderIndex = (int)(Math.random() * allFolders.size());
            String pathToRandomFolder = allFolders.get(randomFolderIndex).toString();
            System.out.println("Random folder " + pathToRandomFolder);
            System.out.println(FeatureDepths.getNumberOfFeatures(projectTree, pathToRandomFolder));
        } catch (IOException exception) { exception.printStackTrace(); }

    }
}