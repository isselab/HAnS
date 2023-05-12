package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class ProjectCloseListener implements ProjectManagerListener{
    private Project project;
    private final MongoDBHandler mongoDBHandler;
    //private final AnnotationEventHandler annotationEventHandler;
    private CustomDocumentListener customDocumentListener;
    private final SessionTracker sessionTracker;
    private HansAnnotationLoggingPage loggingPage;


    public ProjectCloseListener(Project project, CustomDocumentListener customDocumentListener) {
        this.project = project;
        this.mongoDBHandler = new MongoDBHandler();
        this.customDocumentListener = customDocumentListener;
        this.sessionTracker = ApplicationManager.getApplication().getService(SessionTracker.class);
        loggingPage = new HansAnnotationLoggingPage();
    }

    public void projectClosing(@NotNull Project project) {
        System.out.println("ProjectCloseListener: Project closing for: " + project.getName() + " (CustomDocumentListener Instance: " + customDocumentListener.hashCode() + ")");
        customDocumentListener.logTotalTime();
        List<String> logContents = Collections.singletonList(readLogFile());
        mongoDBHandler.insertLogFile(project.getName(), logContents.toString());
        System.out.println("Log contents sent to the database.");
        resetLogFile();
        System.out.println("Log file reset.");
        // Clear the CustomDocumentListener instance
        CustomDocumentListener.clearInstance(project);
        // Reset the SessionTracker
        sessionTracker.resetTotalActiveTime();
    }

    // This method reads the content of the log file "log.json" located at the user's desktop
    // and returns its content as a string
    private String readLogFile() {
        String logFilePath = System.getProperty("java.io.tmpdir") + "/log.json";
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines(Paths.get(logFilePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }

    // This method deletes the log file named "log.json" located at the user's desktop if it exists
    private void resetLogFile() {
        String logFilePath = System.getProperty("java.io.tmpdir") + "/log.json";
        try {
            Files.deleteIfExists(Paths.get(logFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setCustomDocumentListener(CustomDocumentListener customDocumentListener) {
        this.customDocumentListener = customDocumentListener;
    }
    public CustomDocumentListener getCustomDocumentListener() {
        return customDocumentListener;
    }
}
