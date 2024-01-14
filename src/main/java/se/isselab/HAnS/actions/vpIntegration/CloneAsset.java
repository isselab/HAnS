package se.isselab.HAnS.actions.vpIntegration;


import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloneAsset extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        if (virtualFile != null && !virtualFile.isDirectory()) {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null) return;

            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (psiFile != null) {
                processJavaFile(psiFile);
            }
        } else {
            PsiDirectory psiDirectory = PsiManager.getInstance(project).findDirectory(virtualFile);
            if (psiDirectory != null) {
                cloneDirectory(psiDirectory);
            }
        }
        createTrack(anActionEvent);
    }

    public void createTrack(AnActionEvent anActionEvent){
        try {
            String filePath = System.getProperty("user.home") + "\\Documents\\BA\\HAnS\\CloneTrace.txt";
            try{
                String sourceFilePath = new String(Files.readAllBytes(Paths.get(filePath)));
            }catch(Exception e){
                File file = new File(filePath);
            }
            FileWriter fileWriter = new FileWriter(filePath, true);
            VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
            String content = virtualFile.getPath() + ";";
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.newLine();
            bufferFileWriter.append(content);
            bufferFileWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void processJavaFile(PsiFile javaFile) {
        // Iterate over all classes in the file
        for (PsiElement psiElement : javaFile.getChildren()) {
            if (psiElement instanceof PsiClass){
                System.out.println("Class found: " + ((PsiClass) psiElement).getName());
            }
        }
    }

    private void cloneDirectory(PsiDirectory psiDirectory){
        // logic for psiDirectory
        System.out.println("Directory found: " + (psiDirectory.getName()));
    }
}
