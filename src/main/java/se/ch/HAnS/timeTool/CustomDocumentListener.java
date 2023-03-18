package se.ch.HAnS.timeTool;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDocumentListener implements PsiTreeChangeListener {
    final int LOG_INTERVAL = 0; // Milliseconds between being able to log
    private final Project project;
    private final LogWriter logWriter;
    private final CustomTimer timer;
    private boolean featureModelLogged = false;

    public CustomDocumentListener(Project project) {
        this.project = project;
        logWriter = new LogWriter(System.getProperty("user.home") + "/Desktop", "log.txt");
        timer = new CustomTimer();

        // A DocumentListener to detect changes in documents
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {

            // This method is called before a change is made to the document
            @Override
            public void beforeDocumentChange(@NotNull DocumentEvent event) {
                // Get the text that was deleted in the document change event
                Document document = event.getDocument();
                String deletedText = document.getText().substring(event.getOffset(), event.getOffset() + event.getOldLength());

                // Check if the deleted text matches the annotation pattern, including possible leading whitespaces
                Pattern pattern = Pattern.compile("^\\s*//\\s*&");
                Matcher matcher = pattern.matcher(deletedText);
                if (matcher.find()) {
                    // Get the PsiFile associated with the document and retrieve its fileName
                    PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                    if (psiFile != null) {
                        String fileName = psiFile.getName();
                        logWriter.writeToJson(fileName, "annotation", deletedText, timer.getCurrentDate());
                        logWriter.writeToLog("A annotation in a file was removed at " + timer.getCurrentDate() + "\n");
                    }
                }
            }

            // This method is called after the document has been changed
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
            }
        }, new EditorTracker(project));
    }

    // This method processes the file change events and logs specific changes based on the file type
    private void processFileChange(PsiFile psiFile) {
        String fileName = psiFile.getName();
        // If the file is a .feature-model file, and it hasn't been logged yet, log the change
        if (fileName.endsWith(".feature-model") && !featureModelLogged) {
            logWriter.writeToJson(fileName, "annotation",".feature-model", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was edited at " + timer.getCurrentDate() + "\n");
            featureModelLogged = true;
            timer.updateLastLogged();
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    featureModelLogged = false;
                }
            }, ModalityState.NON_MODAL);
        }
    }


    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        if(!timer.canLog(LOG_INTERVAL)){
            return;
        }

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();
        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                logWriter.writeToJson(fileName, "annotation", comment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " added an annotation at " + timer.getCurrentDate() + "\n");
                timer.updateLastLogged();
            }
        }
        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            logWriter.writeToJson(fileName, "annotation","feature-to-file or .feature-to-folder", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was created at " + timer.getCurrentDate() + "\n");
            timer.updateLastLogged();
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        if(!timer.canLog(LOG_INTERVAL)){
            return;
        }

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                logWriter.writeToJson(fileName, "annotation", comment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " removed an annotation at " + timer.getCurrentDate() + "\n");
                timer.updateLastLogged();
            }
        }

        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            logWriter.writeToJson(fileName, "annotation", "feature-to-file or .feature-to-folder", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was removed at " + timer.getCurrentDate() + "\n");
            timer.updateLastLogged();
        }
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        if(!timer.canLog(LOG_INTERVAL)){
            return;
        }

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement oldChild = event.getOldChild();
        PsiElement newChild = event.getNewChild();
        String fileName = event.getFile().getName();

        if (oldChild instanceof PsiComment && newChild instanceof PsiComment) {
            PsiComment oldComment = (PsiComment) oldChild;
            PsiComment newComment = (PsiComment) newChild;
            if (isAnnotationComment(oldComment) || isAnnotationComment(newComment)) {
                logWriter.writeToJson(fileName, "annotation", newComment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " replaced an annotation at " + timer.getCurrentDate() + "\n");
                timer.updateLastLogged();
            }
        }
    }

    // This method checks if the given PsiComment object is an annotation comment (so if it starts with "// &")
    private boolean isAnnotationComment(PsiComment comment) {
        String text = comment.getText();
        return text.startsWith("// &");
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        processFileChange(Objects.requireNonNull(event.getFile()));
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
    }

    // The EditorTracker class is used to track editor events and clean up resources when the project is disposed
    static class EditorTracker implements Disposable {
        private final Project project;

        EditorTracker(Project project) {
            this.project = project;
        }

        @Override
        public void dispose() {
            // Cleanup or unregister resources when the project is disposed
        }
    }
}
