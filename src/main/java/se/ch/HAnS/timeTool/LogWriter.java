package se.ch.HAnS.timeTool;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File logFile;
    private File jsonLog;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    // Class that represents each logged object
    private static class Log {
        private String filename;
        private String type;
        private String details;
        private String timestamp;

        public Log(String filename, String type, String details, String timestamp) {
            this.filename = filename;
            this.type = type;
            this.details = details;
            this.timestamp = timestamp;
        }
    }

    public LogWriter(String path, String name){
        this.logFile = new File(path + "/" + name);
        this.jsonLog = new File(path + "/log.json");
    }

    // Writes to .txt file (delete this method later)
    public void writeToLog(String message){
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
    public void writeToJson(String filename,String type, String details, String timestamp){
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

}
