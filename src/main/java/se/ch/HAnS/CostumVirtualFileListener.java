package se.ch.HAnS;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CostumVirtualFileListener implements VirtualFileListener {
    private final Project project;

    public CostumVirtualFileListener(Project project) {
        this.project = project;
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("File created: " + file.getName() + " at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("File deleted: " + file.getName() + " at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        VirtualFile file = event.getFile();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("File moved: " + file.getName() + " at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        VirtualFile file = event.getFile();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("File contents changed: " + file.getName() + " at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
