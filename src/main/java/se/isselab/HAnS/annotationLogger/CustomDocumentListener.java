package se.isselab.HAnS.annotationLogger;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import net.minidev.json.JSONArray;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * CustomDocumentListener class that tracks and logs changes in the IDE.
 * It implements PsiTreeChangeListener and Disposable to react to code changes and manage resources.
 */
public class CustomDocumentListener implements PsiTreeChangeListener, Disposable {

    private final Project project; // Project in context
    private final LogWriter logWriter; // Used to write logs
    private final CustomTimer timer; // Timer utility for this handler
    private final SessionTracker sessionTracker; // Used to track the software development session
    JSONArray sessionTimes = new JSONArray(); // An array that all the sessions are stored inside of
    private final CustomDocumentEventListener customDocumentEventListener; // A listener that tracks the instant deletion of an annotation
    final int LOG_INTERVAL = 0; // Milliseconds between being able to log
    private final AnnotationEventHandler annotationEventHandler; // The handler for annotation events
    private static final Key<CustomDocumentListener> CUSTOM_DOCUMENT_LISTENER_KEY = Key.create("CustomDocumentListener"); //  Key for the instance of CustomDocumentListener stored in the project.
    private static final HashMap<Project, CustomDocumentListener> instances = new HashMap<>(); // The map storing the instances of the CustomDocumentListener for each project.
    private long highlightStartTime; // The time when highlighting started.
    private boolean userHighlighting = true; // Flag indicating whether the user is highlighting or not.

    /**
     * Constructor for the CustomDocumentListener class.
     * Initializes necessary fields and sets up event listeners.
     *
     * @param project The current project.
     */
    private CustomDocumentListener(Project project) {
        System.out.println("CustomDocumentListener initialized for: " + project.getName() + " (Instance: " + this.hashCode() + ")");
        this.project = project;
        logWriter = new LogWriter(project, System.getProperty("java.io.tmpdir") , "log.txt");
        timer = new CustomTimer();
        sessionTracker = ApplicationManager.getApplication().getService(SessionTracker.class);
        this.annotationEventHandler = new AnnotationEventHandler(project, logWriter, timer, sessionTimes);
        this.customDocumentEventListener = new CustomDocumentEventListener(project, logWriter, timer, annotationEventHandler);
        EditorTracker editorTracker = new EditorTracker(project);

        // Register CustomDocumentListener as a PsiTreeChangeListener associate it with a Disposable
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, this);

        // Schedule a task to check for annotation time periodically
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(this::logAndResetAnnotationSessionIfInactive, 0, 1, TimeUnit.SECONDS);

        // A VirtualFileListener to track the creation and deletion of files
        VirtualFileManager.getInstance().addVirtualFileListener(new CustomVirtualFileListener(logWriter, timer), editorTracker);

        // A DocumentListener to detect changes in documents
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new CustomDocumentEventListener(project, logWriter, timer, annotationEventHandler), editorTracker);

        // Register mouse listeners to all open editors
        Editor[] editors = EditorFactory.getInstance().getAllEditors();
        for (Editor editor : editors) {
            if (editor.getProject() == project) {
                registerMouseListeners(editor);
            }
        }
    }

    /**
     * Registers mouse listeners to an editor.
     * The listeners log events related to highlighting and right-clicking for &like and &block annotations.
     * This is then used to calculate the time the user takes when highlighting a text
     * and then instantly create an annotation.
     *
     * @param editor The editor to which the listeners will be registered.
     */
    private void registerMouseListeners(Editor editor) {
        editor.getContentComponent().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && userHighlighting) {
                    long deletionStartTime = System.currentTimeMillis();
                    annotationEventHandler.setDeletionStartTime(Instant.ofEpochMilli(deletionStartTime));
                    highlightStartTime = System.currentTimeMillis();
                    annotationEventHandler.setHighlightStartTime(Instant.ofEpochMilli(highlightStartTime));

                    // set the starting line number
                    annotationEventHandler.setHighlightedStartLineNumber(editor.xyToLogicalPosition(e.getPoint()).line);
                }
                // If it's a right click, set the right click start time
                else if (SwingUtilities.isRightMouseButton(e)) {
                    long rightClickTime = System.currentTimeMillis();
                    annotationEventHandler.setRightClickStartTime(Instant.ofEpochMilli(rightClickTime));

                    // set the right clicked line number
                    //annotationEventHandler.setRightClickedLineNumber(editor.xyToLogicalPosition(e.getPoint()).line);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && userHighlighting) {
                    long highlightEndTime = System.currentTimeMillis();
                    long highlightingTime = highlightEndTime - highlightStartTime;
                    // updates the end line number at mouse release
                    annotationEventHandler.setHighlightedEndLineNumber(editor.xyToLogicalPosition(e.getPoint()).line);
                }
            }
        });

        editor.getContentComponent().addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && userHighlighting) {
                    // updates the end line number during mouse drag
                    annotationEventHandler.setHighlightedEndLineNumber(editor.xyToLogicalPosition(e.getPoint()).line);
                }
            }
        });

        // KeyListener to reset the highlight start time if a key is pressed
        editor.getContentComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Reset the highlight start time
                annotationEventHandler.setHighlightStartTime(Instant.ofEpochMilli(System.currentTimeMillis()));
            }
        });
    }

    /**
     * Provides the singleton instance of CustomDocumentListener associated with a given project.
     *
     * @param project The current project.
     * @return The CustomDocumentListener instance.
     */
    public static CustomDocumentListener getInstance(Project project) {
        if (!instances.containsKey(project)) {
            synchronized (CustomDocumentListener.class) {
                if (!instances.containsKey(project)) {
                    instances.put(project, new CustomDocumentListener(project));
                }
            }
        }
        return instances.get(project);
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

    /**
     * Method called when a child is added to the PSI tree.
     * Checks if a new annotation comment has been added and logs the event.
     *
     * @param event The event representing the addition of a new child to the PSI tree.
     */
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        if (!timer.canLog(LOG_INTERVAL)) {
            return;
        }    // If not enough time has passed to log, returns early

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();

        if (psiElement instanceof PsiComment) {
            PsiComment comment = (PsiComment) psiElement;
            if (isAnnotationComment(comment)) {
                handleAnnotationCommentEvent(comment, "added", fileName);
            }
        }
    }

    /**
     * Method called when a child is removed from the PSI tree.
     * Checks if an annotation comment has been removed and logs the event.
     *
     * @param event The event representing the removal of a child from the PSI tree.
     */
    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {

    }

    /**
     * Method called when a child in the PSI tree is replaced.
     * Checks if an annotation comment has been replaced and logs the event.
     *
     * @param event The event representing the replacement of a child in the PSI tree.
     */
    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        if (!timer.canLog(LOG_INTERVAL)) {
            return;
        }    // If not enough time has passed to log, returns early

        processFileChange(Objects.requireNonNull(event.getFile()));
        PsiElement oldChild = event.getOldChild();
        PsiElement newChild = event.getNewChild();
        String fileName = event.getFile().getName();

        if (oldChild instanceof PsiComment && newChild instanceof PsiComment) {
            PsiComment oldComment = (PsiComment) oldChild;
            PsiComment newComment = (PsiComment) newChild;
            if (isAnnotationComment(oldComment) || isAnnotationComment(newComment)) {
                handleAnnotationCommentEvent(newComment, "replaced", fileName);
            }
        }
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

    /**
     * Checks if a given PsiComment is an annotation comment.
     *
     * @param comment The PsiComment to check.
     * @return true if the comment is an annotation comment; false otherwise.
     */
    private boolean isAnnotationComment(PsiComment comment) {
        String text = comment.getText();
        return text.startsWith("// &");
    }

    /**
     * Handles annotation comments such as adding, removing, or replacing.
     *
     * @param comment The PsiComment on which the event occurred.
     * @param eventType The type of event ("added", "removed", "replaced").
     * @param fileName The name of the file containing the comment.
     */
    private void handleAnnotationCommentEvent(PsiComment comment, String eventType, String fileName) {
        annotationEventHandler.handleAnnotationCommentEvent(comment, eventType, fileName);
    }

    /**
     * Checks for inactivity in the annotation session.
     * If no annotation activity is detected for more than 10 seconds, logs the total time spent and resets variables.
     */
    private void logAndResetAnnotationSessionIfInactive() {
        annotationEventHandler.logAndResetAnnotationSessionIfInactive();
    }

    /**
     * Processes file change events and logs specific changes based on the file type.
     *
     * @param psiFile The file that was changed.
     */
    private void processFileChange(PsiFile psiFile) {
        annotationEventHandler.processFileChange(psiFile);
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    /**
     * EditorTracker class tracks editor events and disposes resources when the project is terminated.
     */
    static class EditorTracker implements Disposable {
        private final Project project;
        private final Disposable myDisposable = Disposer.newDisposable();

        EditorTracker(Project project) {
            this.project = project;
            // Listen for new editor instances and register the mouse listeners to them
            EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener() {
                @Override
                public void editorCreated(@NotNull EditorFactoryEvent event) {
                    Editor editor = event.getEditor();
                    if (editor.getProject() == project) {
                        CustomDocumentListener.getInstance(project).registerMouseListeners(editor);
                    }
                }

                @Override
                public void editorReleased(@NotNull EditorFactoryEvent event) {
                }
            }, myDisposable);
        }

        @Override
        public void dispose() {
            // Dispose of the Disposable we created
            Disposer.dispose(myDisposable);
        }
    }

    /**
     * Logs the total time spent for each annotation type and the developing time, and adds the current time and date.
     */
    public void logTotalTime() {

        logWriter.writeTotalTimeToJson("&line", (annotationEventHandler.getLineAnnotationTotalTime() + annotationEventHandler.getDeletionTime("line")) + " ms");
        logWriter.writeTotalTimeToJson("&block", (annotationEventHandler.getBlockAnnotationTotalTime() + annotationEventHandler.getDeletionTime("end") + annotationEventHandler.getDeletionTime("begin")) + " ms");
        logWriter.writeTotalTimeToJson(".feature-to-file", annotationEventHandler.getFeatureToFileTotalTime() + " ms");
        logWriter.writeTotalTimeToJson(".feature-to-folder", annotationEventHandler.getFeatureToFolderTotalTime() + " ms");
        logWriter.writeTotalTimeToJson(".feature-model", annotationEventHandler.getFeatureModelTotalTime() + " ms");
        logWriter.writeTotalTimeToJson("Total annotation time", annotationEventHandler.getTotalTime()+ " ms");
        logWriter.writeTotalTimeToJson("Total deletion time", customDocumentEventListener.getTotalDeletionTime() + annotationEventHandler.getDeletionTime("line") + annotationEventHandler.getDeletionTime("end")+" ms");
        logWriter.writeTotalTimeToJson("Total annotation + deletion time", annotationEventHandler.getTotalTime() + annotationEventHandler.getDeletionTime("line") + annotationEventHandler.getDeletionTime("end")+" ms");
        logWriter.writeTotalTimeToJson("Developing time", sessionTracker.getTotalActiveTime() + " ms");
        logWriter.writeToJsonCurrentTime(timer.getCurrentDate());
    }

    /**
     * Clears the CustomDocumentListener instance associated with a given project.
     *
     * @param project The project whose CustomDocumentListener instance will be cleared.
     */
    public static void clearInstance(Project project) {
        project.putUserData(CUSTOM_DOCUMENT_LISTENER_KEY, null);
    }
}