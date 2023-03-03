package se.ch.HAnS.timeTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class LogWriter {
    private File logFile;
    private Writer writer;

    public LogWriter(String name, String path){
        this.logFile = new File(path + "/" + name);

        try{
            this.writer = new FileWriter(logFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToLog(String message){
        try {
            writer.write(message);
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
