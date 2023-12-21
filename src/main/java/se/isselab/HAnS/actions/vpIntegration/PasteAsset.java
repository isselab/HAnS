package se.isselab.HAnS.actions.vpIntegration;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PasteAsset extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        VirtualFile targetVirtualFile = anActionEvent.getData(CommonDataKeys.VIRTUAL_FILE);
        if (targetVirtualFile == null) return;

        String targetFilePath = targetVirtualFile.getPath();
        String textFilePath = System.getProperty("user.home") + "\\Desktop\\myClone.txt";
        String currentDateAndTime = getCurrentDateAndTime();
        try {
            String sourceFilePath = new String(Files.readAllBytes(Paths.get(textFilePath)));
            String[] pahtSplitted = sourceFilePath.split("/");
            String updatedContent = targetFilePath + "\\" + pahtSplitted[pahtSplitted.length - 1] + currentDateAndTime;
            FileWriter fileWriter = new FileWriter(textFilePath, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.append(updatedContent);
            bufferFileWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // Messages.showMessageDialog(date, "Title", Messages.getInformationIcon());
    }

    @Override
    public void update(@NotNull AnActionEvent e){
        super.update(e);
        e.getPresentation().setEnabledAndVisible(false);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        boolean isDirectory = virtualFile != null && virtualFile.isDirectory();
        e.getPresentation().setEnabledAndVisible(isDirectory);
    }

    public String getCurrentDateAndTime(){
        Date time = new Date();
        String date = new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime());
        String[] dateSplitted = date.split("/");
        String currentTime = (time.getTime() / 1000 / 60 / 60) % 24 + "" + (time.getTime() / 1000 / 60) % 60 + "" + (time.getTime() / 1000) % 60;
        String dateAndTimeInString = dateSplitted[2] + dateSplitted[0] + dateSplitted[1] + currentTime;
        return dateAndTimeInString;
    }
}
