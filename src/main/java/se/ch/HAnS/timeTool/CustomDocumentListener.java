package se.ch.HAnS.timeTool;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

public class CustomDocumentListener implements PsiTreeChangeListener {
    private final Project project;
    private final LogWriter logWriter;
    private final CustomTimer timer;
    private boolean featureModelLogged = false;

    public CustomDocumentListener(Project project) {
        this.project = project;
        logWriter = new LogWriter(System.getProperty("user.home") + "/Desktop", "log.txt");
        timer = new CustomTimer();

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String text = event.getDocument().getText(); // get text from a document
                String fileName = event.getDocument().toString(); // can add this inside the writeToLog method to get file location
                int length = event.getDocument().getTextLength(); // get length of the document
                int offset = event.getOffset(); // get offset of the change
                int changeLength = event.getNewLength(); // get length of the change

                if (timer.canLog(5000)) {
                    // log when "line[]" annotation is edited
                    if (text.contains("&line[]")) {
                        logWriter.writeToLog(" editing a &line[] at " + timer.getCurrentDate() + "\n");
                        timer.updateLastLogged();
                    }
                    // log when "begin[]" and "end[]" annotations are edited
                    else if (text.contains("&begin[]") || text.contains("&end[]")) {
                        logWriter.writeToLog(" editing a &begin[] and &end[] at " + timer.getCurrentDate() + "\n");
                        timer.updateLastLogged();
                    }
                    // log when a code line is edited
                    else if (changeLength > 0 && !text.substring(offset, offset + changeLength).matches("\\s*") && !fileName.contains(".feature-model")) {
                        logWriter.writeToLog(" editing a code line at " + timer.getCurrentDate() + "\n");
                        timer.updateLastLogged();
                    }
                }
            }
        });

        registerVirtualFileListener();
    }

    private void registerVirtualFileListener() {
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void propertyChanged(VirtualFilePropertyEvent event) {
            }

            @Override
            public void contentsChanged(VirtualFileEvent event) {
                VirtualFile file = event.getFile();
                String fileName = file.getName();
                if (fileName.endsWith(".feature-model") && !featureModelLogged) {
                    String timestamp = timer.getCurrentDate();

                    logWriter.writeToLog(fileName + " was edited at " + timestamp + "\n");
                    featureModelLogged = true;

                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            featureModelLogged = false;
                        }
                    }, ModalityState.NON_MODAL);
                }
            }
        });
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
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        String timestamp = timer.getCurrentDate();
        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            logWriter.writeToLog(fileName + " was created at " + timestamp + "\n");
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        String timestamp = timer.getCurrentDate();

        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            logWriter.writeToLog(fileName + " was removed at " + timestamp + "\n");
        }
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
    }
}
