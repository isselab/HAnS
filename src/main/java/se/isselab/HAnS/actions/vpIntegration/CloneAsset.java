package se.isselab.HAnS.actions.vpIntegration;


import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
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
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        processJavaFile(psiFile);
        createTrack(anActionEvent);
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());

        //Messages.showMessageDialog("Hello", "Title", Messages.getInformationIcon());
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
        for (PsiElement psiClass : javaFile.getChildren()) {
            if(psiClass instanceof PsiClass){
                System.out.println("Class found: " + ((PsiClass) psiClass).getName());
            }

            // Iterate over all methods in the class
            /*
            for (PsiMethod psiMethod : psiClass.getMethods()) {
                System.out.println("  Method found: " + psiMethod.getName());
            }*/
        }
    }
}