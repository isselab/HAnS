package se.isselab.HAnS.annotationLogger;

import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import org.jetbrains.annotations.NotNull;

/**
 * Class that listens for file-related events and logs them.
 */
class CustomVirtualFileListener implements VirtualFileListener {

    private final LogWriter logWriter; // Used to write logs
    private final CustomTimer timer; // Timer utility for this handler

    /**
     * Constructs a new CustomVirtualFileListener with a specified LogWriter and CustomTimer.
     *
     * @param logWriter The LogWriter used for writing logs.
     * @param timer The CustomTimer used for tracking time.
     */
    public CustomVirtualFileListener(LogWriter logWriter, CustomTimer timer) {
        this.logWriter = logWriter;
        this.timer = timer;
    }

    /**
     * Function for file creation events.
     * Logs the event if the created file's name ends with .feature-model, .feature-to-file or .feature-to-folder.
     *
     * @param event The VirtualFileEvent that contains information about the event.
     */
    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        String fileName = event.getFileName();

        if (fileName.endsWith(".feature-model") || fileName.endsWith(".feature-to-file") || fileName.endsWith(".feature-to-folder")) {
            logWriter.writeToJson(fileName, "annotation", fileName + " created", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was created at " + timer.getCurrentDate() + "\n");
        }
    }

    /**
     * Function for file deletion events.
     * Logs the event if the deleted file's name ends with .feature-model, .feature-to-file or .feature-to-folder.
     *
     * @param event The VirtualFileEvent that contains information about the event.
     */
    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        String fileName = event.getFileName();

        if (fileName.endsWith(".feature-model") || fileName.endsWith(".feature-to-file") || fileName.endsWith(".feature-to-folder")) {
            logWriter.writeToJson(fileName, "annotation", fileName + " deleted", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was deleted at " + timer.getCurrentDate() + "\n");
        }
    }
}
