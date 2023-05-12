package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.time.Duration;
import java.time.Instant;

public class AnnotationEventHandler {
    private final Project project;
    private final LogWriter logWriter;
    private final CustomTimer timer;
    private final JSONArray sessionTimes;

    private static final String LINE_ANNOTATION = "// &l";
    private static final String END_ANNOTATION = "// &e";
    private static final String BEGIN_ANNOTATION = "// &b";

    private static final String FEATURE_MODEL = ".feature-model";
    private static final String FEATURE_TO_FILE = ".feature-to-file";
    private static final String FEATURE_TO_FOLDER = ".feature-to-folder";
    final int LOG_INTERVAL = 0; // Milliseconds between being able to log
    private long lineAnnotationCurrentSessionTime = 0;
    private long blockAnnotationCurrentSessionTime = 0;
    private long featureToFileCurrentSession = 0;
    private long featureToFolderCurrentSession = 0;
    private long featureModelCurrentSession = 0;

    private long lineAnnotationTotalTime = 0;
    private long blockAnnotationTotalTime = 0;
    private long featureToFileTotalTime = 0;
    private long featureToFolderTotalTime = 0;
    private long featureModelTotalTime = 0;

    private Instant lineAnnotationStartTime = null;
    private Instant blockAnnotationStartTime = null;
    private Instant featureToFileStartTime = null;
    private Instant featureToFolderStartTime = null;
    private Instant featureModelStartTime = null;


    public AnnotationEventHandler(Project project, LogWriter logWriter, CustomTimer timer) {
        this.project = project;
        this.logWriter = logWriter;
        this.timer = timer;
        this.sessionTimes = new JSONArray();
    }



     // This method checks so that if there has been no annotation activity for more than 10 seconds, it logs the
     // total time spent during that annotation session, and resets the time variables and updates the log files

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


    // This method handles annotation comment events such as adding, removing, or replacing of an annotation comment
    public void handleAnnotationCommentEvent(PsiComment comment, String eventType, String fileName) {
        String annotationType = getAnnotationType(comment.getText());
        //logWriter.writeToJson(fileName, annotationType, comment.getText(), timer.getCurrentDate());

        Instant currentTime = Instant.now();

        if (annotationType.equals("line[] annotation")) {
            if (lineAnnotationStartTime != null) {
                lineAnnotationTotalTime += Duration.between(lineAnnotationStartTime, currentTime).toMillis();
                lineAnnotationCurrentSessionTime += Duration.between(lineAnnotationStartTime, currentTime).toMillis();

            }
            lineAnnotationStartTime = currentTime;
        } else if (annotationType.equals("begin[] annotation") || annotationType.equals("end[] annotation")) {
            if (blockAnnotationStartTime != null) {
                blockAnnotationTotalTime += Duration.between(blockAnnotationStartTime, currentTime).toMillis();
                blockAnnotationCurrentSessionTime += Duration.between(blockAnnotationStartTime, currentTime).toMillis();
            }
            blockAnnotationStartTime = currentTime;
        }
    }

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

    public boolean isAnnotationFile(String fileName) {
        return fileName.endsWith(FEATURE_MODEL)
                || fileName.endsWith(FEATURE_TO_FILE)
                || fileName.endsWith(FEATURE_TO_FOLDER);
    }

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

    public long getLineAnnotationTotalTime() {
        return lineAnnotationTotalTime;
    }

    public long getBlockAnnotationTotalTime() {
        return blockAnnotationTotalTime;
    }

    public long getFeatureToFileTotalTime() {
        return featureToFileTotalTime;
    }

    public long getFeatureToFolderTotalTime() {
        return featureToFolderTotalTime;
    }

    public long getFeatureModelTotalTime() {
        return featureModelTotalTime;
    }

    private void storeSessionTime(String type, long duration) {
        JSONObject sessionObject = new JSONObject();
        sessionObject.put("type", type);
        sessionObject.put("duration (ms)", duration);

        sessionTimes.add(sessionObject);
    }
    public long getTotalTime(){
        return lineAnnotationTotalTime + blockAnnotationTotalTime + featureToFileTotalTime + featureToFolderTotalTime + featureModelTotalTime;

    }
}