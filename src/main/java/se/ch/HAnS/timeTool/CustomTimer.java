package se.ch.HAnS.timeTool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomTimer {
    private long lastLogged = 0; // Last time something was logged

    // Returns true if enough time has passed since the time passed to the function, else returns false
    public boolean canLog(int interval){    // interval is time in milliseconds
        long currentTime = System.currentTimeMillis();
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
