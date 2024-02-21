package se.isselab.HAnS.assetsManagement.cloningAssets;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationFeatureImpl;

import java.util.ArrayList;
import java.util.List;

public class CloningProjectMenuHandler {

    public static void handleProjectMenu(AnActionEvent anActionEvent, Project project, TracingHandler tracingHandler){
        FeaturesHandler featuresHandler = new FeaturesHandler(project);
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        if (virtualFile != null && !virtualFile.isDirectory()) {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null) return;
            PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
            //PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (psiFile != null) {
                cloneFile(psiFile, featuresHandler);
            }
        } else {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (psiDirectory != null) {
                cloneDirectory(psiDirectory, featuresHandler);
            }
        }
        AssetsToClone.subAssetTrace = tracingHandler.createFileOrFolderTrace();
        tracingHandler.createFeatureTraces();
    }

    public static void cloneFile(PsiFile file, FeaturesHandler featuresHandler) {
        Project project = file.getProject();
        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
        // Extract content from the original file
        String fileContent = file.getText();
        // Create a new file with the same content
        PsiFile newFile = fileFactory.createFileFromText(file.getName(), file.getFileType(), fileContent);
        AssetsToClone.clonedFile = newFile;
        saveExtractedFeatureAnnotations(file);
        AssetsToClone.featuresAnnotations = featuresHandler.findFeatureToFileMappings(file);
        if(AssetsToClone.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsToClone.featuresAnnotations);
        }
    }

    public static void cloneDirectory(PsiDirectory psiDirectory, FeaturesHandler featuresHandler){
        PsiDirectory newDirectory = psiDirectory;
        AssetsToClone.clonedDirectory = newDirectory;
        AssetsToClone.featuresAnnotations = featuresHandler.findFeatureToFolderMappings(psiDirectory);
        if(AssetsToClone.featuresAnnotations != null){
            FeaturesCodeAnnotations.getInstance().addFeatures(AssetsToClone.featuresAnnotations);
        }
    }

    private static void saveExtractedFeatureAnnotations(PsiElement element){
        var featuresAnnotated = extractFeatureNames(element);
        if(featuresAnnotated != null )
            FeaturesCodeAnnotations.getInstance().setFeatureNames(featuresAnnotated);
    }

    private static List<String> extractFeatureNames(PsiElement elements) {
        List<String> featureNames = new ArrayList<>();

        for (PsiElement element : PsiTreeUtil.findChildrenOfType(elements, PsiElement.class)) {
            if (element instanceof PsiComment) {
                if (element instanceof PsiLanguageInjectionHost) {
                    PsiLanguageInjectionHost host = (PsiLanguageInjectionHost) element;
                    InjectedLanguageManager manager = InjectedLanguageManager.getInstance(host.getProject());
                    List<PsiElement> injectedElements = new ArrayList<>();
                    manager.enumerate(host, (injectedPsi, places) -> {
                        injectedElements.addAll(PsiTreeUtil.collectElementsOfType(injectedPsi, CodeAnnotationFeatureImpl.class));
                    });
                    for(PsiElement el : injectedElements){
                        if(!featureNames.contains(el.getText()))
                            featureNames.add(el.getText());
                    }
                }
            }
        }
        if(featureNames.size() != 0)
            return featureNames;
        return null;
    }
}
