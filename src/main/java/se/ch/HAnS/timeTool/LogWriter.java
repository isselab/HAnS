package se.ch.HAnS.timeTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {
    private File logFile;

    public LogWriter(String path, String name){
        this.logFile = new File(path + "/" + name);

    }

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

}
