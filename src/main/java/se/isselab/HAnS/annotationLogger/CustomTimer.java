package se.isselab.HAnS.annotationLogger;

import java.time.Instant;
import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class representing a custom timer that logs events with a specific interval.
 */
public class CustomTimer {
    private Instant lastLogged; // Last time something was logged
    private long idleTime; // The date format used for logging purposes

    /**
     * Constructor that initializes the timer with the current time.
     */
    public CustomTimer() {
        lastLogged = Instant.now();
        idleTime = 0;
    }

    /**
     * Checks if enough time has passed since the last logged time.
     *
     * @param interval The required time interval in milliseconds.
     * @return True if the time elapsed since the last log is greater than or equal to the given interval.
     */
    public boolean canLog(long interval) {
        return Duration.between(lastLogged, Instant.now()).toMillis() >= interval; // interval is time in milliseconds
    }

    /**
     * Updates the last logged time to the current time and adds the elapsed time to the idle time.
     */
    public void updateLastLogged() {
        Instant currentTime = Instant.now();
        idleTime += Duration.between(lastLogged, currentTime).toMillis();
        lastLogged = currentTime;
    }

    /**
     * Returns the total idle time in milliseconds.
     *
     * @return The total idle time.
     */
    public long getIdleTime() {
        return idleTime;
    }

    /**
     * Resets the idle time to zero.
     */
    public void resetIdleTime() {
        idleTime = 0;
    }

    /**
     * Returns the current date and time as a string formatted as "yyyy-MM-dd HH:mm:ss.SSS".
     *
     * @return The current date and time.
     */
    public String getCurrentDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
        return formatter.format(Instant.now());
    }
}
