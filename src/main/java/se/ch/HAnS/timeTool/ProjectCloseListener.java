package se.ch.HAnS.timeTool;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ProjectComponent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class ProjectCloseListener implements ProjectManagerListener, ProjectComponent {
    private Project project;
    private final MongoDBHandler mongoDBHandler;
    //private final AnnotationEventHandler annotationEventHandler;
    private final CustomDocumentListener customDocumentListener;


    public ProjectCloseListener(Project project, CustomDocumentListener customDocumentListener) {
        this.project = project;
        this.mongoDBHandler = new MongoDBHandler();
        this.customDocumentListener = customDocumentListener;
    }

    public void projectClosed(@NotNull Project project) {
        System.out.println("Project closed");
        customDocumentListener.logTotalTime();
        List<String> logContents = Collections.singletonList(readLogFile());
        mongoDBHandler.insertLogFile(project.getName(), logContents.toString());
        System.out.println("Log contents sent to the database.");
        resetLogFile();
        System.out.println("Log file reset.");
    }

    @Override
    public void initComponent() {
        ProjectManager.getInstance().addProjectManagerListener(project, this);
    }

    @Override
    public void disposeComponent() {
        ProjectManager.getInstance().removeProjectManagerListener(project, this);
    }

    // This method reads the content of the log file "log.json" located at the user's desktop
    // and returns its content as a string
    private String readLogFile() {
        String logFilePath = System.getProperty("user.home") + "/Desktop/log.json";
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
        String logFilePath = System.getProperty("user.home") + "/Desktop/log.json";
        try {
            Files.deleteIfExists(Paths.get(logFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
