package se.ch.HAnS.annotationLogger;


import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.intellij.openapi.project.Project;
import net.minidev.json.JSONArray;


import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File logFile;
    private File jsonLog;
    private File jsonSessionLog;

    private final MongoDBHandler mongoDBHandler;
    private final Project project;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public LogWriter(Project project, String path, String name) {
        this.project = project;
        this.logFile = new File(path + "/" + name);
        this.jsonLog = new File(path + "/log.json");
        this.jsonSessionLog = new File(path + "/logSession.json");
        this.mongoDBHandler = new MongoDBHandler();
    }

    // Class that represents each logged object
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

    // Class that represents each logged annotation and its total time
    private static class TotalTimeLog {
        private String annotationType;
        private String totalTime;

        public TotalTimeLog(String annotationType, String totalTime) {
            this.annotationType = annotationType;
            this.totalTime = totalTime;
        }
    }

    // Writes to .txt file (delete this method later)
    public void writeToLog(String message) {
        try {
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(message);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Writes a log to the json-file
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
