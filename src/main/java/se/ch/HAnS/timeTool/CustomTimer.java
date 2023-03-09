package se.ch.HAnS.timeTool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomTimer {
    final int LOG_INTERVAL = 20000; // How long time in milliseconds needs to be passed since the last logged time
    private long lastLogged = 0; // Last time something was logged

    LogWriter logWriter = new LogWriter("log.txt", System.getProperty("user.home") + "/Desktop");

    // Returns true if enough time has passed since the time passed to the function, else returns false
    public boolean canLog(int interval){    // interval is time in milliseconds
        long currentTime = System.currentTimeMillis();
        boolean test = (currentTime - lastLogged) >= interval;
        logWriter.writeToLog(String.valueOf(test));
        return (currentTime - lastLogged) >= interval;
    }

    // Returns the current date as a string with the precision of milliseconds
    public String getCurrentDate(){
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

        return formatter.format(now);
    }

    // Updates the lastLogged variable to the current time
    public void updateLastLogged(){
        lastLogged = System.currentTimeMillis();
    }

}
