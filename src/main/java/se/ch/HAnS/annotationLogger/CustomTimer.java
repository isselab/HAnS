package se.ch.HAnS.annotationLogger;

import java.time.Instant;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CustomTimer {
    private Instant lastLogged; // Last time something was logged
    private long idleTime; // The date format used for logging purposes

    public CustomTimer() {
        lastLogged = Instant.now();
        idleTime = 0;
    }

    // Returns true if enough time has passed since the time passed to the function, else returns false
    public boolean canLog(long interval) {
        return Duration.between(lastLogged, Instant.now()).toMillis() >= interval; // interval is time in milliseconds
    }

    // Updates the lastLogged variable to the current time
    public void updateLastLogged() {
        Instant currentTime = Instant.now();
        idleTime += Duration.between(lastLogged, currentTime).toMillis();
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
        return formatter.format(Instant.now());
    }
}
