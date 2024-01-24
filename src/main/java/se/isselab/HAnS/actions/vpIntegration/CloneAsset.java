package se.isselab.HAnS.actions.vpIntegration;


import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CloneAsset extends AnAction {
    public static PsiFile clonedFile;
    public static PsiDirectory clonedDirectory;
    public static List<String> featureNames;

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        Project project = anActionEvent.getProject();
        VirtualFile virtualFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        if (virtualFile != null && !virtualFile.isDirectory()) {
            Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
            if (editor == null) return;

            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
            if (psiFile != null) {
                cloneFile(psiFile);
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
            String filePath = System.getProperty("user.home") + "\\Documents\\BA\\HAnS\\trace-db.txt";
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

    public static void cloneFile(PsiFile javaFile) {
        Project project = javaFile.getProject();
        PsiFileFactory fileFactory = PsiFileFactory.getInstance(project);
        // Extract content from the original file
        String fileContent = javaFile.getText();
        // Create a new file with the same content
        PsiFile newFile = fileFactory.createFileFromText(javaFile.getName(), javaFile.getFileType(), fileContent);
        clonedFile = newFile;
        featureNames = extractFeatureNames(javaFile);
    }

    public static void cloneDirectory(PsiDirectory psiDirectory){
        PsiDirectory newDirectory = psiDirectory;
        clonedDirectory = newDirectory;
    }

    public static List<String> extractFeatureNames(PsiFile file){
        List<String> featureNames = new ArrayList<>();
        Pattern pattern = Pattern.compile("// &(?:line|begin)\\[(.*?)\\]");

        // Iterate through all elements in the PsiFile
        for (PsiElement element : PsiTreeUtil.findChildrenOfType(file, PsiElement.class)) {
            // Check if the element is a comment
            if (element instanceof PsiComment) {
                String commentText = element.getText();
                Matcher matcher = pattern.matcher(commentText);

                // Check if the comment matches the feature name pattern
                if (matcher.find()) {
                    // Extract the feature name and add it to the list
                    featureNames.add(matcher.group(1));
                }
            }
        }
        return featureNames;
    }
}
