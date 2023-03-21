package se.ch.HAnS.timeTool;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomDocumentListener implements PsiTreeChangeListener {
    final int LOG_INTERVAL = 0; // Milliseconds between being able to log
    private final Project project;
    private final LogWriter logWriter;
    private final CustomTimer timer;
    private long firstLoggedTime = -1;
    private long latestLoggedTime = -1;
    private long lastAnnotationLoggedTime = -1;
    private long totalTimeAnnotation = 0;

    public CustomDocumentListener(Project project) {
        this.project = project;
        logWriter = new LogWriter(System.getProperty("user.home") + "/Desktop", "log.txt");
        timer = new CustomTimer();

        // Schedule a task to check for annotation time periodically
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(this::checkAnnotationTime, 0, 1, TimeUnit.SECONDS);

        // A VirtualFileListener to track the creation and deletion of files
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                onFileCreated(event);
            }

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent event) {
                onFileDeleted(event);
            }
        }, new EditorTracker(project));

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
                        logWriter.writeToJson(fileName, "annotation", "Deleted annotation: " + deletedText, timer.getCurrentDate());
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
        if (!timer.canLog(10)) {
            return;
        }  // If not enough time has passed to log, returns early

        if (isAnnotationFile(fileName)) {
            logWriter.writeToJson(fileName, "annotation", fileName + " changed", timer.getCurrentDate());
            timer.updateLastLogged();
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
        if (!timer.canLog(LOG_INTERVAL)) {
            return;
        }     // If not enough time has passed to log, returns early

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                logWriter.writeToJson(fileName, "annotation", comment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " added an annotation at " + timer.getCurrentDate() + "\n");
                timer.updateLastLogged();
                timer.resetIdleTime();

                // Update firstLoggedTime and latestLoggedTime
                long currentTime = System.currentTimeMillis();
                if (firstLoggedTime == -1) {
                    firstLoggedTime = currentTime;
                }
                latestLoggedTime = currentTime;
                lastAnnotationLoggedTime = currentTime;
            }
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        if (!timer.canLog(LOG_INTERVAL)) {
            return;
        }     // If not enough time has passed to log, returns early

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                logWriter.writeToJson(fileName, "annotation", comment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " removed an annotation at " + timer.getCurrentDate() + "\n");
                timer.updateLastLogged();
                timer.resetIdleTime();

                // Update firstLoggedTime and latestLoggedTime
                long currentTime = System.currentTimeMillis();
                if (firstLoggedTime == -1) {
                    firstLoggedTime = currentTime;
                }
                latestLoggedTime = currentTime;
                lastAnnotationLoggedTime = currentTime;
            }
        }
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        if (!timer.canLog(LOG_INTERVAL)) {
            return;
        }     // If not enough time has passed to log, returns early

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
                timer.resetIdleTime();

                // Update firstLoggedTime and latestLoggedTime
                long currentTime = System.currentTimeMillis();
                if (firstLoggedTime == -1) {
                    firstLoggedTime = currentTime;
                }
                latestLoggedTime = currentTime;
                lastAnnotationLoggedTime = currentTime;
            }
        }
    }

    // This method checks if the given PsiComment object is an annotation comment (so if it starts with "// &")
    private boolean isAnnotationComment(PsiComment comment) {
        String text = comment.getText();
        return text.startsWith("// &");
    }

    // checks if the newest written character is code
    private boolean isCode(char c) {
        if (c == '/') {
            return false;
        }
        if (c == ' ') {
            return false;
        }
        if (c == '\n') {
            return false;
        }
        return true;
    }

    // checks if a string has an annotation file ending
    private boolean isAnnotationFile(String s) {
        if (s.endsWith(".feature-model")) {
            return true;
        }
        if (s.endsWith(".feature-to-file")) {
            return true;
        }
        if (s.endsWith(".feature-to-folder")) {
            return true;
        }
        return false;
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

    // This method checks if the file created ends with ".feature-to-file" or ".feature-to-folder"
    private void onFileCreated(@NotNull VirtualFileEvent event) {
        String fileName = event.getFileName();

        if (fileName.endsWith(".feature-model") || fileName.endsWith(".feature-to-file") || fileName.endsWith(".feature-to-folder")) {
            logWriter.writeToJson(fileName, "annotation", fileName + " created", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was created at " + timer.getCurrentDate() + "\n");
        }
    }

    // This method checks if the file deleted ends with ".feature-to-file" or ".feature-to-folder"
    private void onFileDeleted(@NotNull VirtualFileEvent event) {
        String fileName = event.getFileName();

        if (fileName.endsWith(".feature-model") || fileName.endsWith(".feature-to-file") || fileName.endsWith(".feature-to-folder")) {
            logWriter.writeToJson(fileName, "annotation", fileName + " deleted", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was deleted at " + timer.getCurrentDate() + "\n");
        }
    }

    /*
     * This method checks so that if there has been no annotation activity for more than 10 seconds, it logs the
     * total time spent during that annotation session, and resets the time variables and updates the log files
     */
    private void checkAnnotationTime() {
        long currentTime = System.currentTimeMillis();
        if (lastAnnotationLoggedTime != -1 && currentTime - lastAnnotationLoggedTime >= 10000) {
            if (firstLoggedTime != -1 && latestLoggedTime != -1) {
                long totalTime = latestLoggedTime - firstLoggedTime;
                totalTimeAnnotation += totalTime; // Update totalTimeAnnotation
                logWriter.writeToJson("Total_time_session", "annotation", totalTime + " ms", timer.getCurrentDate());
                logWriter.writeToLog("Total time spent annotating: " + totalTime + " ms" + "\n");
                logWriter.writeToJson("Total_time_annotation", "annotation", totalTimeAnnotation + " ms", timer.getCurrentDate());
            }
            firstLoggedTime = -1;
            latestLoggedTime = -1;
            lastAnnotationLoggedTime = -1;
        }
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
