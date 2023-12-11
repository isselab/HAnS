package se.isselab.HAnS;

import java.text.SimpleDateFormat;

/**
 * Log helper for printing system output with timestamp
 */
public class Logger {
    public static void print (String message) {
        String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").format(new java.util.Date());
        System.out.println(timeStamp + ": " + message);
    }
}
