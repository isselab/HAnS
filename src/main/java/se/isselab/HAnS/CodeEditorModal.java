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
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.LanguageTextField;
import org.bouncycastle.jcajce.provider.symmetric.ARIA;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.codeAnnotation.CodeAnnotationLanguage;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import javax.print.Doc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CodeEditorModal extends DialogWrapper {
    Project project;
    public CustomEditorField codeTextArea1;
    public CustomEditorField codeTextArea2;
    JLabel header1;
    JLabel header2;
    private ArrayList<FeatureAnnotationToDelete> files;
    private int currentIndex;
    JPanel panel;
    GridBagConstraints constraints;
    JButton nextButton;
    JLabel titleLabel;

    private FeatureAnnotationToDelete currentElement;

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
        String code = codeTextArea1.getText();
        // Add logic to save the code
        dispose();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        System.out.println("createCenterPanel() ====== ");
//        panel = new JPanel(new BorderLayout());
//        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel = new JPanel(new GridBagLayout());
        System.out.println(panel.getWidth());
        System.out.println(panel.getHeight());

        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        constraints.fill = GridBagConstraints.NONE;
//        constraints.insets = new Insets(5, 5, 5, 5);

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
//        createNewCodeTextArea1();
//        createNewCodeTextArea2();

//        panel.add(codeTextArea, BorderLayout.CENTER);
//        panel.add(titleLabel, BorderLayout.NORTH);
        constraints.gridy = 9;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        panel.add(nextButton, constraints);
        return panel;
    }

    private void createNewCodeTextArea1(boolean one) {

        if (codeTextArea1 != null) {
            panel.remove(codeTextArea1);
        }
        Language javaLanguage = Language.findLanguageByID("JAVA");
        codeTextArea1 = new CustomEditorField(javaLanguage, this.project);

        constraints.gridy = 2;
        if (one) {
            constraints.gridheight = 7;
        } else {
            constraints.gridheight = 3;
        }
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        panel.add(codeTextArea1, constraints);
    }


    private void createNewCodeTextArea2() {
        if (codeTextArea2 != null) {
            panel.remove(codeTextArea2);
        }
        Language javaLanguage = Language.findLanguageByID("JAVA");
        codeTextArea2 = new CustomEditorField(javaLanguage, this.project);

        constraints.gridy = 6;
        constraints.gridheight = 3;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        constraints.fill = GridBagConstraints.BOTH;
        panel.add(codeTextArea2, constraints);
    }

    private void createHeader1(String featureLpq, String path) {
        if (header1!= null) {
            panel.remove(header1);
        }
        constraints.gridy = 1;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        header1 = new JLabel("<html><div style='margin: 10px; text-align:left;'>Feature " + featureLpq + " in " + path +"</div></html>");
        header1.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(header1, constraints);
    }

    private void createHeader2(String featureLpq, String path) {
        if (header2 != null) {
            panel.remove(header2);
        }
        constraints.gridy = 5;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        header2 = new JLabel("<html><div style='margin: 10px; text-align:left;'>Feature " + featureLpq + " in " + path +"</div></html>");
        header2.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(header2, constraints);
    }


    private void createNewTitle() {
        if (titleLabel != null) {
            panel.remove(titleLabel);
        }
        VirtualFile psiFile = FileDocumentManager.getInstance().getFile(currentElement.getDocument());
        String documentUri = psiFile.getCanonicalPath();
        String[] parts = documentUri.split("/");
        String result = psiFile.getName();
        if (parts.length >= 2) {
            result = "/" + parts[parts.length - 2] + "/" + parts[parts.length - 1];
        }
        String num = currentIndex+1 + "/" + this.files.size();
        String text = "<html><div style='margin: 10px 10px 0 10px; text-align:left;'>" +
                num + ": Feature " + currentElement.getMainFeatureLPQ() + " was tangled with " +
                currentElement.getTangledFeatureLPQ() + " using " + currentElement.getTangledAnnotationType() + " annotation " + " in file " + result + ". <br/>"
                + "Please untangle the features to proceed with deletion. </div></html>";
        titleLabel = new JLabel(text, SwingConstants.CENTER);

        // Set the font and size of the label
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(titleLabel, constraints);
    }
    private void onNextButtonClicked() {
        if (currentIndex < this.files.size()-1) {
            currentIndex += 1;
            currentElement = this.files.get(currentIndex);
//            Document currentElementDoc = currentElement.getDocument();

            System.out.println(currentIndex);
            System.out.println(currentElement);

            createNewTitle();
//            createNewCodeTextArea1();
//            createNewCodeTextArea2();

            setCodeTextArea();


//            setCodeTextArea(codeTextArea1, currentElementDoc, PsiDocumentManager.getInstance(project).getPsiFile(currentElementDoc).getFileType());
//            setCodeTextArea(codeTextArea2, currentElementDoc, PsiDocumentManager.getInstance(project).getPsiFile(currentElementDoc).getFileType());
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
        return codeTextArea1;
    }
    @Override
    protected void init() {
        super.init();
        System.out.println("init ===== ");
        // Set the location of the dialog to be centered on the screen
        centerRelativeToParent();
    }

    private void updateCodeTextArea(CustomEditorField codeTextArea, Document document, int start, int end) {
        codeTextArea.setText(document.getText());
        codeTextArea.setDocument(document);
        codeTextArea.setFileType(PsiDocumentManager.getInstance(project).getPsiFile(document).getFileType());

        codeTextArea.getEditor().getCaretModel().moveToOffset(0);
        codeTextArea.selectText(start, end);
    }

    public void setCodeTextArea() {
        System.out.println("setCodeTextArea ====");

        if (currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.code) &&
            currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.code)) {
            createNewCodeTextArea1(true);

            if (codeTextArea2 != null) {
                panel.remove(codeTextArea2);
            }
            Document doc = currentElement.getDocument();
            createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());
            if (header2 != null) {panel.remove(header2);};
            updateCodeTextArea(codeTextArea1, doc, doc.getLineStartOffset(currentElement.getStartLine()), doc.getLineEndOffset(currentElement.getEndLine()));

        } else {
            createNewCodeTextArea1(false);
            createNewCodeTextArea2();

            if (currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.code) &&
                    currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.file)) {

                updateCodeTextArea(codeTextArea1, currentElement.getDocument(),
                        currentElement.getDocument().getLineStartOffset(currentElement.getStartLine()),
                        currentElement.getDocument().getLineEndOffset(currentElement.getEndLine()));

                createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(currentElement.getDocument()).getPath());

                ArrayList<Object> tangled = getFileFolder(currentElement.getTangledFeatureLPQ());
                Document doc = (Document) tangled.get(0);
                updateCodeTextArea(codeTextArea2, doc, (int) tangled.get(1), (int) tangled.get(2));
                createHeader2(currentElement.getTangledFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());

            } else if (currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.file) &&
                    currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.code) ) {

                ArrayList<Object> tangled = getFileFolder(currentElement.getMainFeatureLPQ());
                Document doc = (Document) tangled.get(0);
                updateCodeTextArea(codeTextArea1, doc, (int) tangled.get(1), (int) tangled.get(2));

                createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());

                updateCodeTextArea(codeTextArea2, currentElement.getDocument(),
                        currentElement.getDocument().getLineStartOffset(currentElement.getStartLine()),
                        currentElement.getDocument().getLineEndOffset(currentElement.getEndLine()));

                createHeader2(currentElement.getTangledFeatureLPQ(), FileDocumentManager.getInstance().getFile(currentElement.getDocument()).getPath());

            } else if (currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.file) &&
                    currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.file)) {
                ArrayList<Object> tangled = getFileFolder(currentElement.getMainFeatureLPQ());
                Document doc = (Document) tangled.get(0);
                updateCodeTextArea(codeTextArea1, doc, (int) tangled.get(1), (int) tangled.get(2));
                createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());

                ArrayList<Object> tangled2 = getFileFolder(currentElement.getTangledFeatureLPQ());
                Document doc2 = (Document) tangled2.get(0);
                updateCodeTextArea(codeTextArea2, doc2, (int) tangled2.get(1), (int) tangled2.get(2));
                createHeader2(currentElement.getTangledFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc2).getPath());

            }
        }

    }

    public void setFileList(Set<FeatureAnnotationToDelete> filesSet) {
        ArrayList<FeatureAnnotationToDelete> files = new ArrayList<>(filesSet);

        System.out.println(Arrays.toString(files.toArray()));
        this.files = files;
        this.currentIndex = 0;
        currentElement = this.files.get(currentIndex);

        System.out.println(currentIndex);
        System.out.println(currentElement);
//        Document currentElementDoc = currentElement.getDocument();

        createNewTitle();

        setCodeTextArea();
//        setCodeTextArea(codeTextArea1, currentElementDoc, PsiDocumentManager.getInstance(project).getPsiFile(currentElementDoc).getFileType());
//        setCodeTextArea(codeTextArea2, currentElementDoc, PsiDocumentManager.getInstance(project).getPsiFile(currentElementDoc).getFileType());
    }

    private ArrayList<Object> getFileFolder(String featureLpq) {
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(currentElement.getDocument());
        VirtualFile parentFolder = virtualFile.getParent();
        ArrayList<Object> result = new ArrayList<>();
        if (parentFolder != null) {
            Arrays.stream(parentFolder.getChildren()).forEach(child -> {
                if (!child.isDirectory()) {
                    if (child.getPath().contains(".feature-to-file")) {
                        Document childFile = FileDocumentManager.getInstance().getDocument(child);
                        if (childFile != null) {
                            int index = childFile.getText().indexOf(featureLpq);

                            while (index >= 0) {
                                int line = childFile.getLineNumber(index);
                                String substr = childFile.getText().substring(
                                        childFile.getLineStartOffset(line - 1),
                                        childFile.getLineEndOffset(line - 1)
                                );
                                if (substr.contains(virtualFile.getName())) {
                                    result.add(childFile); result.add(index); result.add(index + featureLpq.length());
                                    break;
                                }
                                index = childFile.getText().indexOf(featureLpq, index + 1);
                            }
                        }

                    } else if (child.getPath().contains(".feature-to-folder")) {
                        Document childFolder = FileDocumentManager.getInstance().getDocument(child);
                        if (childFolder != null) {
                            String text = childFolder.getText();
                            int startOffset = text.indexOf(featureLpq);
                            int endOffset = startOffset + featureLpq.length();
                            if (startOffset != -1) {
                                result.add(childFolder); result.add(startOffset); result.add(endOffset);
                            }
                        }
                    }
                }
            });
            if (result.isEmpty()) { // if nothing was found in root folder of tangled feature mapping,
                                    // process some higher level feature-to-folder mappings
                VirtualFile newParent = parentFolder.getParent();
                while (newParent != null) {
                    for(VirtualFile child : newParent.getChildren()) {
                        if (child.getPath().contains(".feature-to-folder")) {
                            Document childFolder = FileDocumentManager.getInstance().getDocument(child);
                            if (childFolder != null) {
                                String text = childFolder.getText();
                                int startOffset = text.indexOf(featureLpq);
                                int endOffset = startOffset + featureLpq.length();
                                if (startOffset != -1) {
                                    result.add(childFolder); result.add(startOffset); result.add(endOffset);
                                    break;
                                }
                            }
                        }
                    }
                    if (!result.isEmpty()) {
                        break;
                    }
                    newParent = newParent.getParent();
                }
            }
        }
        System.out.println(result.get(0));
        System.out.println(result.get(1));
        System.out.println(result.get(2));
        System.out.println(" size " + result.size());

        return result;
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
        this.setPreferredSize(new Dimension(600, 298));

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

