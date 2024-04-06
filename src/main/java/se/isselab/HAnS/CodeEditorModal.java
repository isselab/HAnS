package se.isselab.HAnS;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighterFactory;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LanguageTextField;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.codec.language.bm.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CodeEditorModal extends DialogWrapper {
    Project project;
    public CustomEditorField codeTextArea;
    private ArrayList<ArrayList<Object>> files;
    private int currentIndex;
    JPanel panel;
    JButton nextButton;
    JLabel titleLabel;

    private ArrayList<Object> currentElement;

    public CodeEditorModal(Project project) {
        super(project);
        System.out.println("CodeEditorModal() ====== ");
        System.out.println(project);
        this.project = project;
        setTitle("Resolve tangling conflicts");
        setSize(700, 700);
        init();
    }


    private void saveCode() {
        String code = codeTextArea.getText();
        // Add logic to save the code
        dispose();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        System.out.println("createCenterPanel() ====== ");
        panel = new JPanel(new BorderLayout());

//        Language javaLanguage = Language.findLanguageByID("JAVA");
//        codeTextArea = new CustomEditorField(javaLanguage, this.project);

//        codeTextArea.setOneLineMode(false);
//        codeTextArea.setEnabled(true);
//        codeTextArea.setPreferredSize(new Dimension(600, 600));

        nextButton = new JButton("Next");

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the "Next" button click
                onNextButtonClicked();
            }
        });
        createNewCodeTextArea();

//        panel.add(codeTextArea, BorderLayout.CENTER);
//        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(nextButton, BorderLayout.SOUTH);
        return panel;
    }
    private void createNewCodeTextArea() {
        if (codeTextArea!= null) {
            panel.remove(codeTextArea);
        }
        Language javaLanguage = Language.findLanguageByID("JAVA");
        codeTextArea = new CustomEditorField(javaLanguage, this.project);

        panel.add(codeTextArea, BorderLayout.CENTER);
    }
    private void createNewTitle() {
        if (titleLabel != null) {
            panel.remove(titleLabel);
        }
        VirtualFile psiFile = FileDocumentManager.getInstance().getFile((Document)currentElement.get(1));
        String documentUri = psiFile.getCanonicalPath();
        String[] parts = documentUri.split("/");
        String result = psiFile.getName();
        if (parts.length >= 2) {
            result = "/" + parts[parts.length - 2] + "/" + parts[parts.length - 1];
        }
        String num = currentIndex+1 + "/" + this.files.size();
        String text = "<html><div style='text-align: center; margin: 10px; text-align:left;'>" +
                num + ": Feature " + currentElement.get(4) + " was tangled with " +
                currentElement.get(0) + " using " + currentElement.get(5) + " annotation " + " in file " + result + ". <br/>"
                + "Please untangle the features to proceed with deletion. </div></html>";
        titleLabel = new JLabel(text, SwingConstants.CENTER);

        // Set the font and size of the label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);
    }
    private void onNextButtonClicked() {
        if (currentIndex < this.files.size()-1) {
            currentIndex += 1;

            currentElement = this.files.get(currentIndex);
            Document currentElementDoc = (Document) currentElement.get(1);

            System.out.println(currentIndex);
            System.out.println(currentElement);

            createNewCodeTextArea();
            createNewTitle();

            setCodeTextArea(currentElementDoc, PsiDocumentManager.getInstance(project).getPsiFile(currentElementDoc).getFileType());
        } else {
            nextButton.setEnabled(false);
        }

//        String enteredText = codeTextArea.getText();
//        System.out.println("Entered text: " + enteredText);
//        close(DialogWrapper.OK_EXIT_CODE);// Close the dialog
    }


    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        // Set the EditorTextField as the component to focus when the dialog opens
        return codeTextArea;
    }
    @Override
    protected void init() {
        super.init();
        System.out.println("init ===== ");
        // Set the location of the dialog to be centered on the screen
        centerRelativeToParent();
    }

    public void setCodeTextArea(Document document, FileType fileType) {
        System.out.println("setCodeTextArea ====");
        codeTextArea.setText(document.getText());
        codeTextArea.setDocument(document);
        codeTextArea.setFileType(fileType);

        codeTextArea.getEditor().getCaretModel().moveToOffset(0);
        codeTextArea.selectText(codeTextArea.getDocument().getLineStartOffset((Integer) currentElement.get(2)), codeTextArea.getDocument().getLineEndOffset((Integer) currentElement.get(3)));
    }

    public void setFileList(Set<ArrayList<Object>> filesSet) {
        ArrayList<ArrayList<Object>> files = new ArrayList<>(filesSet);

        System.out.println(Arrays.toString(files.toArray()));
        this.files = files;
        this.currentIndex = 0;
        currentElement = this.files.get(currentIndex);
        System.out.println(currentIndex);
        System.out.println(currentElement);
        Document currentElementDoc = (Document) currentElement.get(1);

        createNewTitle();

        setCodeTextArea(currentElementDoc, PsiDocumentManager.getInstance(project).getPsiFile(currentElementDoc).getFileType());
    }
}

class CustomEditorField extends LanguageTextField {
    EditorEx editor;
    Document document;
    Project project;

    public CustomEditorField(Language language, Project project) {

        super(language, project, "");
        System.out.println("CustomEditorField() ====");
        this.project = project;
        this.setOneLineMode(false);
        this.setEnabled(true);
        this.setPreferredSize(new Dimension(600, 600));

        createEditor();
    }

    @Override
    protected @NotNull EditorEx createEditor() {
        System.out.println("createEditor() ===== ");
        editor = super.createEditor();
        EditorSettings settings = editor.getSettings();
        settings.setLineNumbersShown(true);
        settings.setAutoCodeFoldingEnabled(true);
        settings.setFoldingOutlineShown(true);
        settings.setAllowSingleLogicalLineFolding(true);
        settings.setRightMarginShown(true);

        editor.setVerticalScrollbarVisible(true);
        editor.setHorizontalScrollbarVisible(true);
        editor.setCaretEnabled(true);
        editor.setCaretVisible(true);


        EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();

        editor.setColorsScheme(colorsScheme);

//        editor.setHighlighter(EditorHighlighterFactory.getInstance().createEditorHighlighter(project, new LightVirtualFile(fileName)));
//        editor.setHighlighter(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(EditorColorsManager.DEFAULT_SCHEME_NAME).getExternalName());
        editor.setEmbeddedIntoDialogWrapper(true);
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    CodeFoldingManager.getInstance(project).updateFoldRegions(editor);
                }, ModalityState.NON_MODAL);
            }
        });

        return editor;
    }

    @Override
    public void setDocument(Document document) {
        System.out.println("setDocument() =====");
//        this.setText(document.getText());
        this.document = document;
//        super.setText(document.getText());
        super.setDocument(document);
    }

    @Nullable
    @Override
    public EditorEx getEditor() {
        return this.editor;
    }

    public void selectText(int startOffset, int endOffset) {
//        super.getEditor().getSelectionModel().setSelection(startOffset, endOffset);
//        super.getEditor().getCaretModel().moveToOffset(startOffset);
//        super.getEditor().getScrollingModel().scrollToCaret(ScrollType.CENTER);

        editor.getSelectionModel().setSelection(startOffset, endOffset);
        editor.getCaretModel().moveToOffset(startOffset);
//        editor.getCaretModel().moveCaretRelatively(0, 15, false, false, true);
//        int lineToMove = 15;
//        editor.getCaretModel().moveToLogicalPosition(editor.offsetToLogicalPosition(document.getLineStartOffset(lineToMove)));

        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);

    }

//    override fun createEditor(): EditorEx {
//        val editor = super.createEditor()

//        val settings = editor.settings
//        settings.isAutoCodeFoldingEnabled = true
//        settings.isFoldingOutlineShown = true
//        settings.isAllowSingleLogicalLineFolding = true
//        settings.isRightMarginShown=true
//        return editor
//    }
}
