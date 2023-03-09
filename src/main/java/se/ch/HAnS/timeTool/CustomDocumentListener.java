package se.ch.HAnS.timeTool;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorEventMulticaster;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
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

                if (text.contains("&line[]")) {
                    if (timer.canLog(5000)) {  // wait 5 seconds before logging again
                        String timestamp = timer.getCurrentDate();

                        logWriter.writeToLog(fileName + " edit a '&line[]' annotation text at " + timestamp + "\n");
                        timer.updateLastLogged();
                    }
                } else if (text.contains("&begin[]") && text.contains("&end[]")) {
                    if (timer.canLog(5000)) {  // wait 5 seconds before logging again
                        String timestamp = timer.getCurrentDate();
                        logWriter.writeToLog(fileName + " edit a '&begin[]' and '&end[]' annotation text at " + timestamp + "\n");
                        timer.updateLastLogged();
                    }
                } else if (timer.canLog(5000)) {
                    String timestamp = timer.getCurrentDate();
                    logWriter.writeToLog(fileName + " edit a code line at " +  timestamp + "\n");
                    timer.updateLastLogged();
                }
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

                            // Schedule a task to reset featureModelLogged after 2 seconds
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
        PsiElement element = event.getElement();

        String timestamp = timer.getCurrentDate();
        if(timer.canLog(5000)){
            logWriter.writeToLog("PSI element changed at " + timestamp + "\n");

            if (element instanceof PsiFile file) {
                logWriter.writeToLog("File name: " + file.getName() + " was modified.\n");
            } else if (element instanceof PsiDirectory directory) {
                logWriter.writeToLog("Directory name: " + directory.getName() + " was modified.\n");
            } else {
                logWriter.writeToLog("Element name: " + element.toString() + " was modified.\n");
            }
            timer.updateLastLogged();
        }
    }
}
