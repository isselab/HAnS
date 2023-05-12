package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.components.Service;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.time.Instant;
import java.time.Duration;

@Service
public final class SessionTracker {
    // Threshold time (in milliseconds) for considering the user as idle
    private static final int IDLE_THRESHOLD_MS = 30 * 1000; // 30 seconds

    // Last recorded active time
    private Instant lastActiveTime;
    // Total time the user has been active
    private long totalActiveTime;

    public SessionTracker() {
        System.out.println("SessionTracker instantiated.");
        lastActiveTime = Instant.now();  // Initialise the lastActiveTime
        setupListeners();
    }

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

    // Getter method to get the total active time
    public long getTotalActiveTime() {
        return totalActiveTime;
    }
    // Reset method for when we close the project
    public void resetTotalActiveTime() {
        totalActiveTime = 0;
    }
}