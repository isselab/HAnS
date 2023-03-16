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

                    logWriter.writeToJson(fileName, ".feature-model", timer.getCurrentDate());
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
        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                logWriter.writeToJson(fileName, comment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " added an annotation at " + timer.getCurrentDate() + "\n");
            }
        }
        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            logWriter.writeToJson(fileName, "feature-to-file or .feature-to-folder", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was created at " + timer.getCurrentDate() + "\n");
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                logWriter.writeToJson(fileName, comment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " removed an annotation at " + timer.getCurrentDate() + "\n");
            }
        }

        if (fileName.equals(".feature-to-file") && psiElement instanceof PsiFile ||
                fileName.equals(".feature-to-folder") && psiElement instanceof PsiFile) {
            logWriter.writeToJson(fileName, "feature-to-file or .feature-to-folder", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was removed at " + timer.getCurrentDate() + "\n");
        }
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        PsiElement oldChild = event.getOldChild();
        PsiElement newChild = event.getNewChild();
        String fileName = event.getFile().getName();

        if (oldChild instanceof PsiComment && newChild instanceof PsiComment) {
            PsiComment oldComment = (PsiComment) oldChild;
            PsiComment newComment = (PsiComment) newChild;
            if (isAnnotationComment(oldComment) || isAnnotationComment(newComment)) {
                logWriter.writeToJson(fileName, newComment.getText(), timer.getCurrentDate());
                logWriter.writeToLog(fileName + " replaced an annotation at " + timer.getCurrentDate() + "\n");
            }
        }
    }
    private boolean isAnnotationComment(PsiComment comment) {
        String text = comment.getText();
        return text.startsWith("// &");
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
