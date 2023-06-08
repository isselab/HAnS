package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * AnnotationEventHandler handles events related to annotations.
 * It keeps track of the time spent on each type of annotation
 * and provides methods to retrieve this information.
 */
public class AnnotationEventHandler {
    private final Project project; // Project in context
    private final LogWriter logWriter; // Used to write logs
    private final CustomTimer timer; // Timer utility for this handler
    private final JSONArray sessionTimes; // JSON array holding session times

    // Different types of annotation
    private static final String LINE_ANNOTATION = "// &l";
    private static final String END_ANNOTATION = "// &e";
    private static final String BEGIN_ANNOTATION = "// &b";

    // Different types of feature model related files
    private static final String FEATURE_MODEL = ".feature-model";
    private static final String FEATURE_TO_FILE = ".feature-to-file";
    private static final String FEATURE_TO_FOLDER = ".feature-to-folder";

    // Variables for different type of annotation session times
    private long lineAnnotationCurrentSessionTime = 0;
    private long blockAnnotationCurrentSessionTime = 0;
    private long featureToFileCurrentSession = 0;
    private long featureToFolderCurrentSession = 0;
    private long featureModelCurrentSession = 0;

    // Variables for different type of annotation total times
    private long lineAnnotationTotalTime = 0;
    private long blockAnnotationTotalTime = 0;
    private long featureToFileTotalTime = 0;
    private long featureToFolderTotalTime = 0;
    private long featureModelTotalTime = 0;

    // Variables for different type of annotation start times
    private Instant lineAnnotationStartTime = null;
    private Instant blockAnnotationStartTime = null;
    private Instant featureToFileStartTime = null;
    private Instant featureToFolderStartTime = null;
    private Instant featureModelStartTime = null;

    // Variables to track line number and time related to highlight event
    private Instant highlightStartTime = null;
    private Integer highlightedStartLineNumber;
    private Integer highlightedEndLineNumber;

    // Variables to track line number and time related to right-click event
    private Instant rightClickStartTime;
    private Integer rightClickedLineNumber;

    // Variables to track line number and time related to deletion event
    private Instant deletionStartTime;
    private Map<String, Long> deletionTimesPerAnnotation = new HashMap<>();

    //Variables to track the highlighted start time (not currently used)
    public Integer getHighlightedStartLineNumber() {
        return highlightedStartLineNumber;
    }
    public Integer getHighlightedEndLineNumber() {
        return highlightedEndLineNumber;
    }
    public int getRightClickedLineNumber() {
        return rightClickedLineNumber;
    }

    private String lastDeletedAnnotationType;
    private int highlightedLineNumber;
    private Instant deleteStartTime = null;
    //private int rightClickedLineNumber;

    public void setHighlightedStartLineNumber(Integer highlightedStartLineNumber) {
        this.highlightedStartLineNumber = highlightedStartLineNumber;
    }

    public void setHighlightedEndLineNumber(Integer highlightedEndLineNumber) {
        this.highlightedEndLineNumber = highlightedEndLineNumber;
    }

    public void setRightClickedLineNumber(Integer rightClickedLineNumber) {
        this.rightClickedLineNumber = rightClickedLineNumber;
    }


    /**
     * The constructor for AnnotationEventHandler. Initializes the project, log writer, timer, and session times.
     *
     * @param project The current project
     * @param logWriter An instance of LogWriter to write logs
     * @param timer An instance of CustomTimer to track time
     * @param sessionTimes A JSONArray to store session times
     */
    public AnnotationEventHandler(Project project, LogWriter logWriter, CustomTimer timer, JSONArray sessionTimes) {
        this.project = project;
        this.logWriter = logWriter;
        this.timer = timer;
        this.sessionTimes = new JSONArray();
    }



    /**
     * This method checks if there has been no annotation activity for more than 10 seconds, it logs the
     * total time spent during that annotation session, and resets the time variables and updates the log files
     */
    public void logAndResetAnnotationSessionIfInactive() {
        Instant currentTime = Instant.now();
        int idleTime = 10000;

        if (lineAnnotationStartTime != null && Duration.between(lineAnnotationStartTime, currentTime).toMillis() >= idleTime) {
            logWriter.writeToJson("annotation_line", "&line", lineAnnotationCurrentSessionTime + " ms", timer.getCurrentDate());
            storeSessionTime("&line", lineAnnotationCurrentSessionTime);
            logWriter.writeToJson(sessionTimes);
            lineAnnotationCurrentSessionTime = 0;
            lineAnnotationStartTime = null;
        }

        if (blockAnnotationStartTime != null && Duration.between(blockAnnotationStartTime, currentTime).toMillis() >= idleTime) {
            logWriter.writeToJson("annotation_block", "&block", blockAnnotationCurrentSessionTime + " ms", timer.getCurrentDate());
            storeSessionTime("&block", blockAnnotationCurrentSessionTime);
            logWriter.writeToJson(sessionTimes);
            blockAnnotationCurrentSessionTime = 0;
            blockAnnotationStartTime = null;
        }
        if (featureToFileStartTime != null && Duration.between(featureToFileStartTime, currentTime).toMillis() >= idleTime) {
            logWriter.writeToJson(".feature-to-file", "annotation", featureToFileCurrentSession + " ms", timer.getCurrentDate());
            storeSessionTime(".feature-to-file", featureToFileCurrentSession);
            logWriter.writeToJson(sessionTimes);
            featureToFileCurrentSession = 0;
            featureToFileStartTime = null;
        }
        if (featureToFolderStartTime != null && Duration.between(featureToFolderStartTime, currentTime).toMillis() >= idleTime) {
            logWriter.writeToJson(".feature-to-folder", "annotation", featureToFolderCurrentSession + " ms", timer.getCurrentDate());
            storeSessionTime(".feature-to-folder", featureToFolderCurrentSession);
            logWriter.writeToJson(sessionTimes);
            featureToFolderCurrentSession = 0;
            featureToFolderStartTime = null;
        }
        if (featureModelStartTime != null && Duration.between(featureModelStartTime, currentTime).toMillis() >= idleTime) {
            logWriter.writeToJson(".feature-model", "annotation", featureModelCurrentSession + " ms", timer.getCurrentDate());
            storeSessionTime(".feature-model", featureModelCurrentSession);
            logWriter.writeToJson(sessionTimes);
            featureModelCurrentSession = 0;
            featureModelStartTime = null;
        }
    }


    /**
     * Handle annotation events like instant creation of an annotation via
     * "Surround with Feature Annotation" and taking the highlighting as its starting time,
     * also other modification to an annotation.
     *
     * @param comment The comment of the event
     * @param eventType The type of the event
     * @param fileName The name of the file where the event occurred
     */
    public void handleAnnotationCommentEvent(PsiComment comment, String eventType, String fileName) {
        String annotationType = getAnnotationType(comment.getText());
        Instant currentTime = Instant.now();

        if (annotationType.equals("line[] annotation")) {
            if (lineAnnotationStartTime != null) {
                lineAnnotationTotalTime += Duration.between(lineAnnotationStartTime, currentTime).toMillis();
                lineAnnotationCurrentSessionTime += Duration.between(lineAnnotationStartTime, currentTime).toMillis();
            }
            if (highlightStartTime != null ) {
                lineAnnotationStartTime = highlightStartTime;
            } else if (rightClickStartTime != null) {
                lineAnnotationStartTime = rightClickStartTime;
            } else {
                lineAnnotationStartTime = currentTime;
            }
            // reset the time and line number variables
            highlightStartTime = null;
            rightClickStartTime = null;
            highlightedStartLineNumber = null;
            highlightedEndLineNumber = null;
            //rightClickedLineNumber = null;
        }
        else if (annotationType.equals("begin[] annotation") || annotationType.equals("end[] annotation")) {
            if (blockAnnotationStartTime != null) {
                blockAnnotationTotalTime += Duration.between(blockAnnotationStartTime, currentTime).toMillis();
                blockAnnotationCurrentSessionTime += Duration.between(blockAnnotationStartTime, currentTime).toMillis();
            }
            if (highlightStartTime != null) {
                blockAnnotationStartTime = highlightStartTime;
            } else if (rightClickStartTime != null) {
                blockAnnotationStartTime = rightClickStartTime;
            } else {
                blockAnnotationStartTime = currentTime;
            }
            // reset the time and line number variables
            highlightStartTime = null;
            rightClickStartTime = null;
            highlightedStartLineNumber = null;
            highlightedEndLineNumber = null;
            //rightClickedLineNumber = null;
        }
    }

    /**
     * Process changes in a file, specifically "feature-to-file", "feature-to-folder" and "feature-model".
     *
     * @param psiFile The file that changed
     */
    public void processFileChange(PsiFile psiFile) {
        String fileName = psiFile.getName();
        if (!timer.canLog(10)) {
            return;
        }  // If not enough time has passed to log, returns early

        if (isAnnotationFile(fileName)) {
            //logWriter.writeToJson(fileName, "annotation", fileName + " changed", timer.getCurrentDate());
            Instant currentTime = Instant.now();

            if (fileName.endsWith(".feature-to-file")) {
                if (featureToFileStartTime != null) {
                    featureToFileTotalTime += Duration.between(featureToFileStartTime, currentTime).toMillis();
                    featureToFileCurrentSession += Duration.between(featureToFileStartTime, currentTime).toMillis();
                }
                featureToFileStartTime = currentTime;
            }
            if (fileName.endsWith(".feature-to-folder")) {
                if (featureToFolderStartTime != null) {
                    featureToFolderTotalTime += Duration.between(featureToFolderStartTime, currentTime).toMillis();
                    featureToFolderCurrentSession += Duration.between(featureToFolderStartTime, currentTime).toMillis();
                }
                featureToFolderStartTime = currentTime;
            }
            if (fileName.endsWith(".feature-model")) {
                if (featureModelStartTime != null) {
                    featureModelTotalTime += Duration.between(featureModelStartTime, currentTime).toMillis();
                    featureModelCurrentSession += Duration.between(featureModelStartTime, currentTime).toMillis();
                }
                featureModelStartTime = currentTime;
            }
        }
    }

    /**
     * Check if a file is an annotation file based on the name.
     *
     * @param fileName The name of the file
     * @return True if the file is an annotation file, otherwise its false
     */
    public boolean isAnnotationFile(String fileName) {
        return fileName.endsWith(FEATURE_MODEL)
                || fileName.endsWith(FEATURE_TO_FILE)
                || fileName.endsWith(FEATURE_TO_FOLDER);
    }

    /**
     * Get the type of annotation from the text of an annotation.
     *
     * @param annotationText The text of the annotation
     * @return The type of annotation
     */
    public String getAnnotationType(String annotationText) {
        if (annotationText.startsWith(LINE_ANNOTATION)) {
            return "line[] annotation";
        } else if (annotationText.startsWith(END_ANNOTATION)) {
            return "end[] annotation";
        } else if (annotationText.startsWith(BEGIN_ANNOTATION)) {
            return "begin[] annotation";
        } else {
            return "uncertain annotation";
        }
    }

    /**
     * Returns the total time spent on line annotations.
     *
     * @return total time spent on line annotations in milliseconds
     */
    public long getLineAnnotationTotalTime() {
        return lineAnnotationTotalTime;
    }

    /**
     * Returns the total time spent on block annotations.
     *
     * @return total time spent on block annotations in milliseconds
     */
    public long getBlockAnnotationTotalTime() {
        return blockAnnotationTotalTime;
    }

    /**
     * Returns the total time spent on feature to file annotations.
     *
     * @return total time spent on feature to file annotations in milliseconds
     */
    public long getFeatureToFileTotalTime() {
        return featureToFileTotalTime;
    }

    /**
     * Returns the total time spent on feature to folder annotations.
     *
     * @return total time spent on feature to folder annotations in milliseconds
     */
    public long getFeatureToFolderTotalTime() {
        return featureToFolderTotalTime;
    }

    /**
     * Returns the total time spent on feature model annotations.
     *
     * @return total time spent on feature model annotations in milliseconds
     */
    public long getFeatureModelTotalTime() {
        return featureModelTotalTime;
    }

    /**
     * Store the duration of an annotation session.
     *
     * @param type The type of annotation
     * @param duration The duration of the session
     */
    public void storeSessionTime(String type, long duration) {
        JSONObject sessionObject = new JSONObject();
        sessionObject.put("type", type);
        sessionObject.put("duration (ms)", duration);

        sessionTimes.add(sessionObject);
    }

    /**
     * Retrieves all the session times stored.
     *
     * @return A JSONArray containing all the session times
     */
    public JSONArray getSessionTimes() {
        return sessionTimes;
    }

    /**
     * Returns the total time spent on all types of annotations.
     *
     * @return total time spent on all types of annotations in milliseconds
     */
    public long getTotalTime(){
        return lineAnnotationTotalTime + blockAnnotationTotalTime + featureToFileTotalTime + featureToFolderTotalTime + featureModelTotalTime;
    }

    /**
     * Sets the start time of the highlighting.
     *
     * @param startTime The time when the highlight started
     */
    public void setHighlightStartTime(Instant startTime) {
        this.highlightStartTime = startTime;
        //System.out.println("The Highlight start time is set to: " + highlightStartTime);
    }

    /**
     * Handles the time taken for highlighting.
     *
     * @param highlightingTime Time taken for highlighting in milliseconds
     */
    public void handleHighlightingTime(long highlightingTime) {
        // Handle the highlighting time here...
        System.out.println("The highlight time: " + highlightingTime);
    }

    /**
     * Sets the start time of the deletion.
     *
     * @param startTime The time when the deletion started
     */
    public void setDeleteStartTime(Instant startTime) {
        this.deleteStartTime = startTime;
        System.out.println("The deletion start time is set to: " + deleteStartTime);
    }

    /**
     * Handles the time taken for deletion.
     *
     * @param deletionTime Time taken for deletion in milliseconds
     * @param annotationName The name of the annotation being deleted
     */
    public void handleDeletionTime(long deletionTime, String annotationName) {
        System.out.println("The deletion time: " + deletionTime);
        storeSessionTime(annotationName, deletionTime);
        logWriter.writeToJson(sessionTimes);
    }

    /**
     * Sets the start time of the deletion.
     *
     * @param deletionStartTime The time when the deletion started.
     */
    public void setDeletionStartTime(Instant deletionStartTime) {
        this.deletionStartTime = deletionStartTime;
    }

    /**
     * Retrieves the deletion start time for when an annotation is deleted.
     *
     * @return The deletion start time for when an annotation is deleted.
     */
    public Instant getDeletionStartTime() {
        return this.deletionStartTime;
    }

    /**
     * Retrieves the highlighted start time.
     *
     * @return The highlighted start time.
     */
    public Instant getHighlightStartTime() {
        return this.highlightStartTime;
    }

    /**
     * Sets the last deleted annotation type.
     *
     * @param annotationType The type of the last deleted annotation.
     */
    public void setLastDeletedAnnotationType(String annotationType) {
        this.lastDeletedAnnotationType = annotationType;
    }

    /**
     * Retrieves the last deleted annotation type.
     *
     * @return The type of the last deleted annotation.
     */
    public String getLastDeletedAnnotationType() {
        return this.lastDeletedAnnotationType;
    }

    /**
     * Adds the given deletion time to the total deletion time for the given annotation type.
     *
     * @param annotationType The type of the annotation.
     * @param deletionTime The time taken for the deletion in milliseconds.
     */
    public void addDeletionTime(String annotationType, long deletionTime) {
        long currentDeletionTime = deletionTimesPerAnnotation.getOrDefault(annotationType, 0L);
        deletionTimesPerAnnotation.put(annotationType, currentDeletionTime + deletionTime);
    }

    /**
     * Retrieves the total deletion time for the given annotation type.
     *
     * @param annotationType The type of the annotation.
     * @return The total deletion time for the annotation type in milliseconds.
     */
    public long getDeletionTime(String annotationType) {
        return deletionTimesPerAnnotation.getOrDefault(annotationType, 0L);
    }

    /**
     * Sets the right click start time.
     *
     * @param rightClickStartTime The time when the right click started.
     */
    public void setRightClickStartTime(Instant rightClickStartTime) {
        this.rightClickStartTime = rightClickStartTime;
    }

    /**
     * Sets the highlighted line number.
     *
     * @param highlightedLineNumber The highlighted line number.
     */
    public void setHighlightedLineNumber(int highlightedLineNumber) {
        this.highlightedLineNumber = highlightedLineNumber;
    }

    /**
     * Retrieves the highlighted line number.
     *
     * @return The highlighted line number.
     */
    public int getHighlightedLineNumber() {
        return highlightedLineNumber;
    }

    /**
     * Sets the rightclicked line number.
     *
     * @param rightClickedLineNumber The number of the line that was rightclicked.
     */
    public void setRightClickedLineNumber(int rightClickedLineNumber) {
        this.rightClickedLineNumber = rightClickedLineNumber;
    }


}