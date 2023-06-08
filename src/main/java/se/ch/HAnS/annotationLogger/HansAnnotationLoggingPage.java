package se.ch.HAnS.annotationLogger;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import javax.swing.*;
import java.awt.*;

/**
 * Class representing the configuration page for Hans Annotation Logging.
 */
public class HansAnnotationLoggingPage implements Configurable {
    private JPanel panel;
    private JRadioButton mongodbOption;
    private JRadioButton localOption;
    private JRadioButton noLoggingOption;
    private ButtonGroup group;
    private static PropertiesComponent properties = PropertiesComponent.getInstance();
    //private PropertiesComponent properties;
    private static final String LOGGING_PREF_KEY = "loggingPref";

    private JButton chooseDirectoryButton;
    private JLabel selectedDirectoryLabel;
    private static final String LOGGING_DIRECTORY_KEY = "loggingDirectory";


    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Hans Annotation Logging";
    }

    /**
     * Creates and initializes the GUI components for the logging settings page.
     *
     * @return the created component.
     */
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

        System.out.println("properties initialized");
        properties = PropertiesComponent.getInstance();

        chooseDirectoryButton = new JButton("Choose Directory");
        selectedDirectoryLabel = new JLabel();

        panel.add(chooseDirectoryButton);
        panel.add(selectedDirectoryLabel);

        chooseDirectoryButton.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int returnValue = jfc.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                String selectedDirectory = jfc.getSelectedFile().getAbsolutePath();
                selectedDirectoryLabel.setText(selectedDirectory);
                properties.setValue(LOGGING_DIRECTORY_KEY, selectedDirectory);
            }
        });

        return panel;
    }

    /**
     * Checks if any changes have been made on the logging settings page that have not been saved yet.
     *
     * @return true if changes were made, otherwise false.
     */
    @Override
    public boolean isModified() {
        String selected = mongodbOption.isSelected() ? "mongodb" : localOption.isSelected() ? "local" : "none";
        String current = properties.getValue(LOGGING_PREF_KEY, "none");
        return !selected.equals(current);
    }

    /**
     * Called when the user clicks "apply/apply" on the settings page,
     * saves and applies the selected settings to the current project.
     *
     * @throws ConfigurationException if there was a problem with the configuration
     */
    @Override
    public void apply() throws ConfigurationException {
        String selected = mongodbOption.isSelected() ? "mongodb" : localOption.isSelected() ? "local" : "none";
        properties.setValue(LOGGING_PREF_KEY, selected);
    }

    /**
     * Called when the user clicks "reset/cancel" on the settings page.
     * Resets the settings on the page to match the current settings stored in the project.
     */
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

    /**
     * Called when the settings page is closed. Used to clean up resources used by the settings page.
     */
    @Override
    public void disposeUIResources() {
    }

    /**
     * Interface for different logging strategies.
     */
    public interface LogStrategy {
        void log(String message);
    }

    /**
     * Class representing a logging strategy using MongoDB.
     */
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

    /**
     * Class representing a local logging strategy.
     */
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

    /**
     * Class representing a logging strategy that does not perform any logging.
     */
    public static class NoLoggingStrategy implements LogStrategy {
        @Override
        public void log(String message) {

        }
    }

    /**
     * Returns the current logging strategy based on the current settings.
     *
     * @param project the current project
     * @return the current logging strategy
     */
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
                String logDirectory = properties.getValue(LOGGING_DIRECTORY_KEY, System.getProperty("user.home"));
                return new LocalLogStrategy(new LogWriter(project, logDirectory, "annotationLogger.json"));
            default:
                System.out.println("Returning NoLoggingStrategy");
                return new NoLoggingStrategy();
        }
    }
}


