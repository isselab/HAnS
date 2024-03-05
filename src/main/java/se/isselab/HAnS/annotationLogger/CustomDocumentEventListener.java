package se.isselab.HAnS.annotationLogger;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import net.minidev.json.JSONArray;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for handling the instant deletion of an annotation (specifically for the &block and &line annotation)
 */
class CustomDocumentEventListener implements DocumentListener {

    private final Project project; // Project in context
    private final LogWriter logWriter; // Used to write logs
    private final CustomTimer timer; // Timer utility for this handler
    private final JSONArray sessionTimes; // JSON array holding session times
    private final AnnotationEventHandler annotationEventHandler; // The handler for annotation events
    private long totalDeletionTime = 0; // Total deletion time of an annotation

    /**
     * Constructs a new CustomDocumentEventListener with the specified parameters.
     *
     * @param project The current project.
     * @param logWriter The LogWriter used for writing logs.
     * @param timer The CustomTimer used for tracking time.
     * @param annotationEventHandler The AnnotationEventHandler for handling annotation events.
     */
    public CustomDocumentEventListener(Project project, LogWriter logWriter, CustomTimer timer, AnnotationEventHandler annotationEventHandler) {
        this.project = project;
        this.logWriter = logWriter;
        this.timer = timer;
        this.annotationEventHandler = annotationEventHandler;
        this.sessionTimes = new JSONArray();
    }

    /**
     * Called before a document change is made.
     * If the deleted text matches the annotation pattern (&line, &being or &end), the deletion time is calculated
     * and stored. The session times are then written to JSON.
     *
     * @param event The DocumentEvent that contains information about the event.
     */
    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        // Get the text that was deleted in the document change event
        Document document = event.getDocument();
        String deletedText = document.getText().substring(event.getOffset(), event.getOffset() + event.getOldLength());

        // Check if the deleted text matches the annotation pattern, including possible leading whitespaces
        Pattern pattern = Pattern.compile("^\\s*//\\s*&(.+?)\\[");
        Matcher matcher = pattern.matcher(deletedText);
        if (matcher.find()) {
            // Get the PsiFile associated with the document and retrieve its fileName
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null) {
                String fileName = psiFile.getName();
                //String annotationName = matcher.group(1).trim();

                // Calculate deletion time
                Instant deletionEndTime = Instant.now();
                long deletionTime1 = Duration.between(annotationEventHandler.getDeletionStartTime(), deletionEndTime).toMillis();

                // Increment totalDeletionTime
                totalDeletionTime += deletionTime1;

                // Extract the annotation type
                String annotationType = matcher.group(1).trim();
                String annotationName = annotationType;

                if (Arrays.asList("line", "end", "begin").contains(annotationType)) {
                    long deletionTime = Duration.between(annotationEventHandler.getDeletionStartTime(), deletionEndTime).toMillis();
                    annotationEventHandler.addDeletionTime(annotationType, deletionTime);
                }
                // Store session time
                annotationEventHandler.storeSessionTime("Deleted &"+ annotationName, deletionTime1);

                // Write the session times to JSON
                logWriter.writeToJson(annotationEventHandler.getSessionTimes());
            }
        }
    }


    /**
     * Called after a document change is made (currently not used).
     *
     * @param event The DocumentEvent that contains information about the event.
     */
    @Override
    public void documentChanged(@NotNull DocumentEvent event) {

    }

    /**
     * Returns the total deletion time of annotations.
     *
     * @return The total deletion time of annotations.
     */
    public long getTotalDeletionTime() {
        return totalDeletionTime;
    }
}
