package se.ch.HAnS.timeTool;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiFile;

public class AnnotationEventHandler {
    private final Project project;
    private final LogWriter logWriter;
    private final CustomTimer timer;

    private static final String LINE_ANNOTATION = "// &l";
    private static final String END_ANNOTATION = "// &e";
    private static final String BEGIN_ANNOTATION = "// &b";
    private static final String UNCERTAIN_ANNOTATION = "// &";

    private static final String FEATURE_MODEL = ".feature-model";
    private static final String FEATURE_TO_FILE = ".feature-to-file";
    private static final String FEATURE_TO_FOLDER = ".feature-to-folder";

    private static long firstLoggedTime = -1;
    private static long latestLoggedTime = -1;
    private static long lastAnnotationLoggedTime = -1;
    private static long totalTimeAnnotation = 0;
    final int LOG_INTERVAL = 0; // Milliseconds between being able to log

    private long lineAnnotationCurrentSessionTime = 0;
    private long lineAnnotationTotalTime = 0;
    private long lineAnnotationStartTime = -1;
    private long blockAnnotationTotalTime = 0;
    private long blockAnnotationCurrentSessionTime = 0;
    private long blockAnnotationStartTime = -1;
    private long featureToFileCurrentSession = 0;
    private long featureToFileTotalTime = 0;
    private long featureToFileStartTime = -1;
    private long featureToFolderCurrentSession = 0;
    private long featureToFolderTotalTime = 0;
    private long featureToFolderStartTime = -1;
    private long featureModelCurrentSession = 0;
    private long featureModelTotalTime = 0;
    private long featureModelStartTime = -1;


    public AnnotationEventHandler(Project project, LogWriter logWriter, CustomTimer timer) {
        this.project = project;
        this.logWriter = logWriter;
        this.timer = timer;
    }

    public boolean isAnnotationComment(PsiComment comment) {
        String text = comment.getText();
        return text.startsWith(UNCERTAIN_ANNOTATION);
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

    // This method handles annotation comment events such as adding, removing, or replacing of an annotation comment
    public void handleAnnotationCommentEvent(PsiComment comment, String eventType, String fileName) {
        String annotationType = getAnnotationType(comment.getText());
        logWriter.writeToJson(fileName, annotationType, comment.getText(), timer.getCurrentDate());
        logWriter.writeToLog(fileName + " " + eventType + " an annotation at " + timer.getCurrentDate() + "\n");
        timer.updateLastLogged();
        timer.resetIdleTime();

        long currentTime = System.currentTimeMillis();

        if (annotationType.equals("line[] annotation")) {
            if (lineAnnotationStartTime != -1) {
                lineAnnotationTotalTime += currentTime - lineAnnotationStartTime;
                lineAnnotationCurrentSessionTime += currentTime - lineAnnotationStartTime;

            }
            lineAnnotationStartTime = currentTime;
        } else if (annotationType.equals("begin[] annotation") || annotationType.equals("end[] annotation")) {
            if (blockAnnotationStartTime != -1) {
                blockAnnotationTotalTime += currentTime - blockAnnotationStartTime;
                blockAnnotationCurrentSessionTime += currentTime - blockAnnotationStartTime;
            }
            blockAnnotationStartTime = currentTime;
        } else if (fileName.endsWith(".feature-to-file")) {
            if (featureToFileStartTime != -1) {
                featureToFileTotalTime += currentTime - featureToFileStartTime;
                featureToFileCurrentSession += currentTime - featureToFileStartTime;
            }
            featureToFileStartTime = currentTime;
        } else if (fileName.endsWith(".feature-to-folder")) {
            if (featureToFolderStartTime != -1) {
                featureToFolderTotalTime += currentTime - featureToFolderStartTime;
                featureToFolderCurrentSession += currentTime - featureToFolderStartTime;
            }
            featureToFolderStartTime = currentTime;
        } else if (fileName.endsWith(".feature-model")) {
            if (featureModelStartTime != -1) {
                featureModelTotalTime += currentTime - featureModelStartTime;
                featureModelCurrentSession += currentTime - featureModelStartTime;
            }
            featureModelStartTime = currentTime;
        }
    }

    /*
     * This method checks so that if there has been no annotation activity for more than 10 seconds, it logs the
     * total time spent during that annotation session, and resets the time variables and updates the log files
     */
    public void checkAnnotationTime() {
        long currentTime = System.currentTimeMillis();

        if (lineAnnotationStartTime != -1 && currentTime - lineAnnotationStartTime >= 10000) {
            logWriter.writeToJson("Session_time_annotation_line", "annotation", lineAnnotationCurrentSessionTime + " ms", timer.getCurrentDate());
            logWriter.writeToJson("Total_time_annotation_line", "annotation", lineAnnotationTotalTime + " ms", timer.getCurrentDate());
            lineAnnotationCurrentSessionTime = 0;
            lineAnnotationStartTime = -1;
        }

        if (blockAnnotationStartTime != -1 && currentTime - blockAnnotationStartTime >= 10000) {
            logWriter.writeToJson("Session_time_annotation_block", "annotation", blockAnnotationCurrentSessionTime + " ms", timer.getCurrentDate());
            logWriter.writeToJson("Total_time_annotation_block", "annotation", blockAnnotationTotalTime + " ms", timer.getCurrentDate());
            blockAnnotationCurrentSessionTime = 0;
            blockAnnotationStartTime = -1;
        }
        if (featureToFileStartTime != -1 && currentTime - featureToFileStartTime >= 10000) {
            logWriter.writeToJson("Session_time_feature-to-file", "annotation", featureToFileCurrentSession + " ms", timer.getCurrentDate());
            logWriter.writeToJson("Total_time_annotation_feature-to-file", "annotation", featureToFileTotalTime + " ms", timer.getCurrentDate());
            featureToFileCurrentSession = 0;
            featureToFileStartTime = -1;
        }
        if (featureToFolderStartTime != -1 && currentTime - featureToFolderStartTime >= 10000) {
            logWriter.writeToJson("Session_time_feature-to-folder", "annotation", featureToFolderCurrentSession + " ms", timer.getCurrentDate());
            logWriter.writeToJson("Total_time_annotation_feature-to-folder", "annotation", featureToFolderTotalTime + " ms", timer.getCurrentDate());
            featureToFolderCurrentSession = 0;
            featureToFolderStartTime = -1;
        }
        if (featureModelStartTime != -1 && currentTime - featureModelStartTime >= 10000) {
            logWriter.writeToJson("Session_time_feature-model", "annotation", featureModelCurrentSession + " ms", timer.getCurrentDate());
            logWriter.writeToJson("Total_time_annotation_feature-model", "annotation", featureModelTotalTime + " ms", timer.getCurrentDate());
            featureModelCurrentSession = 0;
            featureModelStartTime = -1;
        }
    }

    public void processFileChange(PsiFile psiFile) {
        String fileName = psiFile.getName();
        if (!timer.canLog(10)) {
            return;
        }  // If not enough time has passed to log, returns early

        if (isAnnotationFile(fileName)) {
            logWriter.writeToJson(fileName, "annotation", fileName + " changed", timer.getCurrentDate());
            timer.updateLastLogged();
            timer.resetIdleTime();

            // Update firstLoggedTime and latestLoggedTime
            long currentTime = System.currentTimeMillis();
            if (firstLoggedTime == -1) {
                firstLoggedTime = currentTime;
            }
            latestLoggedTime = currentTime;
            lastAnnotationLoggedTime = currentTime;
            checkAnnotationTime();

            if (fileName.endsWith(".feature-to-file")) {
                if (featureToFileStartTime != -1) {
                    featureToFileTotalTime += currentTime - featureToFileStartTime;
                    featureToFileCurrentSession += currentTime - featureToFileStartTime;
                }
                featureToFileStartTime = currentTime;
            }
            if (fileName.endsWith(".feature-to-folder")) {
                if (featureToFolderStartTime != -1) {
                    featureToFolderTotalTime += currentTime - featureToFolderStartTime;
                    featureToFolderCurrentSession += currentTime - featureToFolderStartTime;
                }
                featureToFolderStartTime = currentTime;
            }
            if (fileName.endsWith(".feature-model")) {
                if (featureModelStartTime != -1) {
                    featureModelTotalTime += currentTime - featureModelStartTime;
                    featureModelCurrentSession += currentTime - featureModelStartTime;
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
}


