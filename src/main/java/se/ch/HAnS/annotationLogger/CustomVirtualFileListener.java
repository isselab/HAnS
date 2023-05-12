package se.ch.HAnS.annotationLogger;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CustomDocumentEventListener implements DocumentListener {

    private final Project project;
    private final LogWriter logWriter;
    private final CustomTimer timer;

    public CustomDocumentEventListener(Project project, LogWriter logWriter, CustomTimer timer) {
        this.project = project;
        this.logWriter = logWriter;
        this.timer = timer;
    }

    // This method is called before a change is made to the document
    @Override
    public void beforeDocumentChange(@NotNull DocumentEvent event) {
        // Get the text that was deleted in the document change event
        Document document = event.getDocument();
        String deletedText = document.getText().substring(event.getOffset(), event.getOffset() + event.getOldLength());

        // Check if the deleted text matches the annotation pattern, including possible leading whitespaces
        Pattern pattern = Pattern.compile("^\\s*//\\s*&(.+?)\\[");
        Matcher matcher = pattern.matcher(deletedText);
        if (matcher.find()) {
            String annotationName = matcher.group(1).trim();
            // Get the PsiFile associated with the document and retrieve its fileName
            PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null) {
                String fileName = psiFile.getName();
                logWriter.writeToJson(fileName, annotationName, "Deleted annotation: " + deletedText, timer.getCurrentDate());
            }
        }
    }

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {

    }
}
