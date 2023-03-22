package se.ch.HAnS.timeTool;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

@Service
public final class SessionTracker {
    // Threshold time (in milliseconds) for considering the user as idle
    private static final int IDLE_THRESHOLD_MS = 5 * 60 * 1000; // 5 minutes

    // Last recorded active time
    private long lastActiveTime;
    // Total time the user has been active
    private long totalActiveTime;

    public SessionTracker() {
        setupListeners();
    }

    private void setupListeners() {
        // Get the event multicaster from the editor factory to listen to editor events
        EditorEventMulticaster eventMulticaster = EditorFactory.getInstance().getEventMulticaster();

        // Add an editor mouse listener to update the lastActiveTime when the user clicks
        eventMulticaster.addEditorMouseListener(new EditorMouseListener() {
            @Override
            public void mouseClicked(@NotNull EditorMouseEvent e) {
                lastActiveTime = System.currentTimeMillis();
            }
        });

        // Add an AWT event listener to monitor focus events for all components
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof FocusEvent) {
                    FocusEvent focusEvent = (FocusEvent) event;
                    // If the focus is gained, update the lastActiveTime
                    if (focusEvent.getID() == FocusEvent.FOCUS_GAINED) {
                        lastActiveTime = System.currentTimeMillis();
                    }
                    // If the focus is lost, calculate the active time and update totalActiveTime
                    else if (focusEvent.getID() == FocusEvent.FOCUS_LOST) {
                        long now = System.currentTimeMillis();
                        long deltaTime = now - lastActiveTime;
                        // Only add the active time if it's below the idle threshold
                        if (deltaTime < IDLE_THRESHOLD_MS) {
                            totalActiveTime += deltaTime;
                        }
                    }
                }
            }
        }, FocusEvent.FOCUS_EVENT_MASK);
    }

    // Getter method to get the total active time
    public long getTotalActiveTime() {
        return totalActiveTime;
    }
}