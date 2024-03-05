package se.isselab.HAnS.annotationLogger;

import com.intellij.openapi.components.Service;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.time.Instant;
import java.time.Duration;
/**
 * This class is responsible for tracking user's active session. It listens to user's mouse and key events and
 * calculates the total active time of the user by considering a threshold of idle time.
 */
@Service
public final class SessionTracker {
    // Threshold time (in milliseconds) for considering the user as idle
    private static final int IDLE_THRESHOLD_MS = 30 * 1000; // 30 seconds

    // Last recorded active time
    private Instant lastActiveTime;
    // Total time the user has been active
    private long totalActiveTime;

    /**
     * Constructs a new SessionTracker and initializes it's fields.
     */
    public SessionTracker() {
        System.out.println("SessionTracker instantiated.");
        lastActiveTime = Instant.now();  // Initialise the lastActiveTime
        setupListeners();
    }

    /**
     * Sets up the listeners for mouse and key events. The listeners update the total active time when an event is dispatched.
     */
    private void setupListeners() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent || event instanceof KeyEvent){
                    Instant now = Instant.now();
                    long deltaTime = Duration.between(lastActiveTime, now).toMillis();
                    lastActiveTime = now;
                    if (deltaTime < IDLE_THRESHOLD_MS){
                        totalActiveTime += deltaTime;
                    }

                }
            }
        }, MouseEvent.MOUSE_EVENT_MASK | KeyEvent.KEY_EVENT_MASK);
    }

    /**
     * Returns the total active time of the user.
     *
     * @return The total active time in milliseconds.
     */
    public long getTotalActiveTime() {
        return totalActiveTime;
    }

    /**
     * Resets the total active time of the user.
     */
    public void resetTotalActiveTime() {
        totalActiveTime = 0;
    }
}