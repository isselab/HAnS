package se.ch.HAnS.annotationLogger;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class HansAnnotationLoggingPage implements Configurable {
    private JPanel panel;
    private JRadioButton mongodbOption;
    private JRadioButton localOption;
    private JRadioButton noLoggingOption;
    private ButtonGroup group;
    private static PropertiesComponent properties = PropertiesComponent.getInstance();
    //private PropertiesComponent properties;
    private static final String LOGGING_PREF_KEY = "loggingPref";



    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Hans Annotation Logging";
    }

    // Creates and setting up the GUI components for the logging settings page (also initializes the necessary components like the buttons and checkboxes)
    @Nullable
    @Override
    public JComponent createComponent() {
        panel = new JPanel(new GridLayout(0, 1));
        mongodbOption = new JRadioButton("Log to MongoDB");
        localOption = new JRadioButton("Log locally");
        noLoggingOption = new JRadioButton("Do not log");
        group = new ButtonGroup();
        group.add(mongodbOption);
        group.add(localOption);
        group.add(noLoggingOption);

        panel.add(mongodbOption);
        panel.add(localOption);
        panel.add(noLoggingOption);

        System.out.println("Properties initialized");
        properties = PropertiesComponent.getInstance();

        return panel;
    }

    // This method checks if any changes have been made on the logging settings page that have not been saved yet, it then compares it then compares the current setting on the page with the one stored in the project(if there is a difference == return true, if not == return false)
    @Override
    public boolean isModified() {
        String selected = mongodbOption.isSelected() ? "mongodb" : localOption.isSelected() ? "local" : "none";
        String current = properties.getValue(LOGGING_PREF_KEY, "none");
        return !selected.equals(current);
    }

    // This method is called whenever a user clicks "apply/apply" on the settings page, it then saves and changes the new current setting on the settings page to the current project.
    @Override
    public void apply() throws ConfigurationException {
        String selected = mongodbOption.isSelected() ? "mongodb" : localOption.isSelected() ? "local" : "none";
        properties.setValue(LOGGING_PREF_KEY, selected);
    }

    // This method is called when the user clicks "reset/cancel" on the settings page. This method just resets the setting on the page to match the current settings stored in the project,
    @Override
    public void reset() {
        String current = properties.getValue(LOGGING_PREF_KEY, "none");
        switch (current) {
            case "mongodb":
                mongodbOption.setSelected(true);
                break;
            case "local":
                localOption.setSelected(true);
                break;
            default:
                noLoggingOption.setSelected(true);
        }
    }
    // This method is called when the settings page is closed. It's used to clean up resources used by the settings page
    @Override
    public void disposeUIResources() {

    }

    public interface LogStrategy {
        void log(String message);
    }
    public static class MongoDBLogStrategy implements LogStrategy {
        private Project project;
        private MongoDBHandler handler;

        public MongoDBLogStrategy(Project project) {
            this.project = project;
            handler = new MongoDBHandler();
        }

        @Override
        public void log(String message) {
            System.out.println("Logging to mongodb"); // testing if it works
            handler.insertLogFile(project.getName(), message);
        }
    }
    public static class LocalLogStrategy implements LogStrategy {
        private LogWriter logWriter;

        public LocalLogStrategy(LogWriter logWriter) {
            this.logWriter = logWriter;
        }

        @Override
        public void log(String message) {
            logWriter.writeToLog(message);
        }
    }
    public static class NoLoggingStrategy implements LogStrategy {
        @Override
        public void log(String message) {

        }
    }

    public LogStrategy getLogStrategy(Project project) {
        if (properties == null) {
            System.out.println("Properties is null");
            return new NoLoggingStrategy();
        }

        String current = properties.getValue(LOGGING_PREF_KEY, "none");
        System.out.println("Current log strategy: " + current);

        switch (current) {
            case "mongodb":
                System.out.println("Returning MongoDBLogStrategy");
                return new MongoDBLogStrategy(project);
            case "local":
                System.out.println("Returning LocalLogStrategy");
                return new LocalLogStrategy(new LogWriter(project, System.getProperty("user.home") + "/Desktop", "annotationLogger.json"));
            default:
                System.out.println("Returning NoLoggingStrategy");
                return new NoLoggingStrategy();
        }
    }
}


