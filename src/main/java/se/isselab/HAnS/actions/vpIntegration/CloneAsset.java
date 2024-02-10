package se.isselab.HAnS.actions.vpIntegration;


import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.codeAnnotation.psi.impl.CodeAnnotationFeatureImpl;
import se.isselab.HAnS.vpIntegration.FeaturesCodeAnnotations;
import se.isselab.HAnS.vpIntegration.FeaturesHandler;
import se.isselab.HAnS.vpIntegration.TracingHandler;
import java.util.ArrayList;
import java.util.List;

public class CloneAsset extends AnAction {
    public static PsiFile clonedFile;
    public static PsiDirectory clonedDirectory;
    public static PsiMethod clonedMethod;
    public static PsiClass clonedClass;
    public static List<PsiElement> elementsInRange;
    public static String subTrace;
    public static ArrayList<PsiElement> featuresAnnotations;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        String place = anActionEvent.getPlace();
        Project project = anActionEvent.getProject();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        TracingHandler tracingHandler = new TracingHandler(anActionEvent);
        resetClones();
        if(place.equals("ProjectViewPopup")){
            //handle Project menu
            handleProjectMenu(anActionEvent, project, tracingHandler);

        } else if(place.equals("EditorPopup")){
            //handle editor menu
            handleEditorMenu(anActionEvent,editor, tracingHandler);
        }
    }

    public static void cloneFile(PsiFile file, FeaturesHandler featuresHandler) {
        Project project = file.getProject();
        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
        // Extract content from the original file
        String fileContent = file.getText();
        // Create a new file with the same content
        PsiFile newFile = fileFactory.createFileFromText(file.getName(), file.getFileType(), fileContent);
        clonedFile = newFile;
        FeaturesCodeAnnotations.getInstance().setFeatureNames(extractFeatureNames(file));
        featuresAnnotations = featuresHandler.findFeatureToFileMappings(file);
    }

    public static void cloneDirectory(PsiDirectory psiDirectory, FeaturesHandler featuresHandler){
        PsiDirectory newDirectory = psiDirectory;
        clonedDirectory = newDirectory;
        featuresAnnotations = featuresHandler.findFeatureToFolderMappings(psiDirectory);
    }


    private void handleProjectMenu(AnActionEvent anActionEvent, Project project, TracingHandler tracingHandler){
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
        subTrace = tracingHandler.createFileOrFolderTrace();
    }

    private void handleEditorMenu(AnActionEvent anActionEvent, Editor editor, TracingHandler tracingHandler){
        //Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;
        PsiFile psiFile = PsiDocumentManager.getInstance(anActionEvent.getProject()).getPsiFile(editor.getDocument());
        if (psiFile == null) return;
        SelectionModel selectionModel = editor.getSelectionModel();
        if(selectionModel.hasSelection()){
            getHighlightedBlock(selectionModel, psiFile);
            tracingHandler.createCodeAssetsTrace();
            return;
        }
        int caretOffset = editor.getCaretModel().getOffset();
        PsiElement element = psiFile.findElementAt(caretOffset);
        PsiMethod methodAtCaret = PsiTreeUtil.getParentOfType(element, PsiMethod.class);
        if(methodAtCaret != null){
            cloneMethod(methodAtCaret);
        } else {
            cloneClass(element);
        }
        subTrace = tracingHandler.createCodeAssetsTrace();
    }

    public static void resetClones() {
        clonedFile = null;
        clonedDirectory = null;
        clonedClass = null;
        clonedMethod = null;
        elementsInRange = null;
        featuresAnnotations = null;
    }

    private void cloneClass(PsiElement element) {
        PsiClass classAtCaret = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        clonedClass = classAtCaret;
        FeaturesCodeAnnotations.getInstance().setFeatureNames(extractFeatureNames(classAtCaret));
    }

    private void cloneMethod(PsiMethod methodAtCaret) {
        clonedMethod = methodAtCaret;
        FeaturesCodeAnnotations.getInstance().setFeatureNames(extractFeatureNames(methodAtCaret));
    }

    private void getHighlightedBlock(SelectionModel selectionModel, PsiFile psiFile){
        // Get start and end offsets of the selected text
        int startOffset = selectionModel.getSelectionStart();
        int endOffset = selectionModel.getSelectionEnd();
        // Get the PSI elements corresponding to these offsets
        PsiElement startElement = psiFile.findElementAt(startOffset);
        PsiElement endElement = psiFile.findElementAt(endOffset);
        // Create a list to hold all PsiElements within the range
        List<PsiElement> elementsInRange = new ArrayList<>();
        // Traverse from startElement to endElement and collect PsiElements
        PsiElement currentElement = startElement;
        while (currentElement != null && !currentElement.equals(endElement)) {
            elementsInRange.add(currentElement);
            currentElement = PsiTreeUtil.nextLeaf(currentElement);
        }
        // Add the endElement to the list if it's not null
        if (endElement != null) {
            elementsInRange.add(endElement);
        }
        if(startElement != null && endElement != null){
            cloneBlock(elementsInRange);
        }

    }

    private static void cloneBlock(List<PsiElement> elements) {
        elementsInRange = elements;
        FeaturesCodeAnnotations.getInstance().setFeatureNames(extractFeaturesOfCodeBlock(elements));
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
        return featureNames;
    }

    private static List<String> extractFeaturesOfCodeBlock(List<PsiElement> elements){
        List<String> featureNames = new ArrayList<>();
        for(PsiElement element : elements){
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
        return featureNames;
    }
}
