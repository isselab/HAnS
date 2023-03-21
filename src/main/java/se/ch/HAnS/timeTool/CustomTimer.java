package se.ch.HAnS.timeTool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomTimer {
    private long lastLogged; // Last time something was logged
    private long idleTime; // The date format used for logging purposes

    public CustomTimer() {
        lastLogged = System.currentTimeMillis();
        idleTime = 0;
    }

    // Returns true if enough time has passed since the time passed to the function, else returns false
    public boolean canLog(long interval) {
        return System.currentTimeMillis() - lastLogged >= interval; // interval is time in millisecond
    }

    // Updates the lastLogged variable to the current time
    public void updateLastLogged() {
        long currentTime = System.currentTimeMillis();
        idleTime += currentTime - lastLogged;
        lastLogged = currentTime;
    }

    public long getIdleTime() {
        return idleTime;
    }

    public void resetIdleTime() {
        idleTime = 0;
    }

    // Returns the current date as a string with the precision of milliseconds
    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(new Date());
    }
}
