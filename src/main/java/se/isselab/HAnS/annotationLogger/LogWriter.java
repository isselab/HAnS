package se.isselab.HAnS.annotationLogger;


import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.intellij.openapi.project.Project;
import net.minidev.json.JSONArray;


import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles all logging operations for the application including writing logs to text files and to JSON files.
 */
public class LogWriter {
    // The file where log entries are written to.
    private File logFile;

    // The file where JSON formatted logs are written to.
    private File jsonLog;

    // The file where JSON formatted logs for sessions are written to.
    private File jsonSessionLog;

    // An instance of MongoDBHandler to handle interactions with MongoDB.
    private final MongoDBHandler mongoDBHandler;

    // The project in the context of which the logs are being written.
    private final Project project;

    // A Gson instance used for converting Java objects into their JSON representation.
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    /**
     * Constructs a new LogWriter.
     *
     * @param project The current project.
     * @param path The path to the directory where logs should be written.
     * @param name The name of the log file.
     */
    public LogWriter(Project project, String path, String name) {
        this.project = project;
        this.logFile = new File(path + "/" + name);
        this.jsonLog = new File(path + "/log.json");
        this.jsonSessionLog = new File(path + "/logSession.json");
        this.mongoDBHandler = new MongoDBHandler();
    }

    /**
     * Class that represents each logged object.
     */
    private static class Log {
        private String filename;
        private String type;
        private String sessionTime;
        private String timestamp;

        public Log(String filename, String type, String sessionTime, String timestamp) {
            this.filename = filename;
            this.type = type;
            this.sessionTime = sessionTime;
            this.timestamp = timestamp;
        }
    }

    /**
     * Class that represents each logged annotation and its total time.
     */
    private static class TotalTimeLog {
        private String annotationType;
        private String totalTime;

        public TotalTimeLog(String annotationType, String totalTime) {
            this.annotationType = annotationType;
            this.totalTime = totalTime;
        }
    }

    /**
     * Writes a log message to a text file.
     *
     * @param message The message to be written.
     */
    public void writeToLog(String message) {
        try {
            System.out.println("Attempting to write to log file at: " + logFile.getAbsolutePath());
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(message);
            writer.flush();
            writer.close();
            System.out.println("Successfully wrote to log file");
        } catch (IOException e) {
            System.out.println("Failed to write to log file");
            e.printStackTrace();
        }
    }


    /**
     * Writes a log to a JSON file.
     *
     * @param filename The name of the file where the event took place.
     * @param type The type of event.
     * @param details Details of the event.
     * @param timestamp The time when the event occurred.
     */
    public void writeToJson(String filename, String type, String details, String timestamp) {
        // Create a Log object with the given parameters
        Log log = new Log(filename, type, details, timestamp);

        try {
            // Create a FileWriter object to write to the JSON file
            FileWriter writer = new FileWriter(jsonLog, true); // "true" appends to file rather than overwriting

            // Convert the Log object to JSON and write it to the file
            gson.toJson(log, writer);
            writer.write(System.lineSeparator()); // Add a new line to separate log entries
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the total time for an annotation type to a JSON file.
     *
     * @param annotationType The type of annotation.
     * @param totalTime The total time for the annotation type.
     */
    public void writeTotalTimeToJson(String annotationType, String totalTime) {
        // Create a TotalTimeLog object with the given parameters
        TotalTimeLog totalTimeLog = new TotalTimeLog(annotationType, totalTime);

        try {
            // Create a FileWriter object to write to the JSON file
            FileWriter writer = new FileWriter(jsonLog, true); // "true" appends to file rather than overwriting

            // Convert the TotalTimeLog object to JSON and write it to the file
            gson.toJson(totalTimeLog, writer);
            writer.write(System.lineSeparator()); // Add a new line to separate log entries
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the current time to a JSON file.
     *
     * @param currentTime The current time.
     */
    public void writeToJsonCurrentTime(String currentTime) {
        try {
            // Create a FileWriter object to write to the JSON file
            FileWriter writer = new FileWriter(jsonLog, true); // "true" appends to file rather than overwriting

            // Convert the TotalTimeLog object to JSON and write it to the file
            gson.toJson("Timestamp: " + currentTime, writer);
            writer.write(System.lineSeparator()); // Add a new line to separate log entries
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes an array of session times to a JSON file.
     *
     * @param sessionTimes An array of session times.
     */
    public void writeToJson(JSONArray sessionTimes) {
        try {
            String tempDirectoryPath = System.getProperty("java.io.tmpdir");
            FileWriter file = new FileWriter(tempDirectoryPath + "log.json");
            file.write(sessionTimes.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
