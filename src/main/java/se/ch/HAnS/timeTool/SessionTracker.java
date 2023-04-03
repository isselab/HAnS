package se.ch.HAnS.timeTool;

import com.intellij.openapi.components.Service;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

@Service
public final class SessionTracker {
    // Threshold time (in milliseconds) for considering the user as idle
    private static final int IDLE_THRESHOLD_MS = 30 * 1000; // 30 seconds

    // Last recorded active time
    private long lastActiveTime;
    // Total time the user has been active
    private long totalActiveTime;

    public SessionTracker() {
        System.out.println("SessionTracker instantiated.");
        setupListeners();
    }

    private void setupListeners() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent || event instanceof KeyEvent){
                    long now = System.currentTimeMillis();
                    long deltaTime = now - lastActiveTime;
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
}