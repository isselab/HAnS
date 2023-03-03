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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CustomDocumentListener implements PsiTreeChangeListener {
    private final Project project;
    int time = 0;
    private long lastLogTime = 0;
    private boolean featureModelLogged = false;

    public CustomDocumentListener(Project project) {
        this.project = project;

        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                String text = event.getDocument().getText();
                String fileName = event.getDocument().toString();
                if (text.contains("&line[]")) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastLogTime > 5000) {  // wait 5 seconds before logging again
                        Date now = new Date();
                        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String timestamp = dateFormat.format(now);
                        try {
                            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
                            FileWriter writer = new FileWriter(logFile, true);
                            writer.write(fileName + " edit a '&line[]' annotation text at " + timestamp + "\n");
                            writer.close();
                            lastLogTime = currentTime;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (text.contains("&begin[]") && text.contains("&end[]")) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastLogTime > 5000) {  // wait 5 seconds before logging again
                        Date now = new Date();
                        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        String timestamp = dateFormat.format(now);

                        try {
                            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
                            FileWriter writer = new FileWriter(logFile, true);
                            writer.write(fileName + " edit a '&begin[]' and '&end[]' annotation text at " + timestamp + "\n");
                            writer.close();
                            lastLogTime = currentTime;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
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
                            Date now = new Date();
                            java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            String timestamp = dateFormat.format(now);

                            try {
                                File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
                                FileWriter writer = new FileWriter(logFile, true);
                                writer.write(fileName + " was edited at " + timestamp + "\n");
                                writer.close();
                                featureModelLogged = true;
                                // Schedule a task to reset featureModelLogged after 2 seconds
                                ApplicationManager.getApplication().invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        featureModelLogged = false;
                                    }
                                }, ModalityState.NON_MODAL);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);
        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            try {
                time = +10;
                File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
                FileWriter writer = new FileWriter(logFile, true);
                writer.write(fileName + " was created at " + timestamp + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            try {
                time = +4;
                File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
                FileWriter writer = new FileWriter(logFile, true);
                writer.write(fileName + " was removed at " + timestamp + "\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("PSI element changed at " + timestamp + "\n");

            if (element instanceof PsiFile file) {
                writer.write("File name: " + file.getName() + " was modified.\n");
            } else if (element instanceof PsiDirectory directory) {
                writer.write("Directory name: " + directory.getName() + " was modified.\n");
            } else {
                writer.write("Element name: " + element.toString() + " was modified.\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
