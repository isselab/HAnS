package se.isselab.HAnS;

import java.text.SimpleDateFormat;

/**
 * Log helper for printing system output with timestamp
 */
public class Logger {
    public enum Channel {ERROR, WARNING, DEBUG, STANDARD}
    public static void printWithTimestamp (String message) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new java.util.Date());
        System.out.println(timeStamp + ": " + message);
    }

    public static void print(Channel channel, String message){
        String name = "HAnS";

        switch (channel){
            case ERROR, WARNING -> {
                System.err.printf("[%s][%s] %s%n", name, channel, message);
            }
            case DEBUG, STANDARD -> {
                System.out.printf("[%s][%s] %s%n", name, channel, message);
            }
        }
    }
}
