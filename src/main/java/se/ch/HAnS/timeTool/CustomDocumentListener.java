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
                String text = event.getDocument().getText();
                String fileName = event.getDocument().toString();
                    // log when "line[]" annotation is edited
                if (text.contains("&line[]")) {
                    String timestamp = timer.getCurrentDate();
                    logWriter.writeToLog(fileName + " editing a &line[] at " +  timestamp + "\n");
                    timer.updateLastLogged();
                    // log when "begin[]" and "end[]" annotations are edited
                } else if (text.contains("&begin[]") && text.contains("&end[]")) {
                    String timestamp = timer.getCurrentDate();
                    logWriter.writeToLog(fileName + " editing a &begin[] and &end[] at " +  timestamp + "\n");
                    timer.updateLastLogged();
                    // editing a file
                } else if (!text.matches("\\s*")) { // check if the added text is not just whitespace
                    if (timer.canLog(5000)) {
                        String timestamp = timer.getCurrentDate();
                        logWriter.writeToLog(fileName + " editing a code line at " +  timestamp + "\n");
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
