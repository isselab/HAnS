package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import org.jetbrains.annotations.NotNull;

class CustomVirtualFileListener implements VirtualFileListener {

    private final LogWriter logWriter;
    private final CustomTimer timer;

    public CustomVirtualFileListener(LogWriter logWriter, CustomTimer timer) {
        this.logWriter = logWriter;
        this.timer = timer;
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        String fileName = event.getFileName();

        if (fileName.endsWith(".feature-model") || fileName.endsWith(".feature-to-file") || fileName.endsWith(".feature-to-folder")) {
            logWriter.writeToJson(fileName, "annotation", fileName + " created", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was created at " + timer.getCurrentDate() + "\n");
        }
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        String fileName = event.getFileName();

        if (fileName.endsWith(".feature-model") || fileName.endsWith(".feature-to-file") || fileName.endsWith(".feature-to-folder")) {
            logWriter.writeToJson(fileName, "annotation", fileName + " deleted", timer.getCurrentDate());
            logWriter.writeToLog(fileName + " was deleted at " + timer.getCurrentDate() + "\n");
        }
    }
}
