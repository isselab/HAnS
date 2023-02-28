package se.ch.HAnS;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class CustomDocumentListener implements PsiTreeChangeListener {
    private final Project project;

    public CustomDocumentListener(Project project) {
        this.project = project;
    }


    @Override
    public void beforeChildAddition(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildRemoval(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildReplacement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildMovement(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforeChildrenChange(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void beforePropertyChange(@NotNull PsiTreeChangeEvent event) {

    }



    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(fileName + " was created at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(fileName + " was removed at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        PsiElement psiElement = event.getChild();
        String fileName = psiElement.getContainingFile().getName();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write(fileName + " was changed at " + timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {

    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        PsiElement element = event.getElement();
        Date now = new Date();
        java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String timestamp = dateFormat.format(now);

        try {
            File logFile = new File(System.getProperty("user.home") + "/Desktop/log.txt");
            FileWriter writer = new FileWriter(logFile, true);
            writer.write("PSI element changed at " + timestamp + "\n");

            if (element instanceof PsiFile file) {
                writer.write("File name: " + file.getName() + " was modified.\n");
            } else if (element instanceof PsiDirectory directory) {
                writer.write("Directory name: " + directory.getName() + " was modified.\n");
            } else {
                writer.write("Element name: " + element.toString() + " was modified.\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
