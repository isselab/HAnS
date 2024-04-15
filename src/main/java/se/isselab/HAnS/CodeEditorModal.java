package se.isselab.HAnS;

import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.ui.LanguageTextField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class CodeEditorModal extends DialogWrapper {
    Project project;
    // 1st file editor with header
    JLabel header1;
    public CustomEditorField codeTextArea1;
    // 2nd file editor with header
    JLabel header2;
    public CustomEditorField codeTextArea2;
    // top header
    JLabel titleLabel;
    // files of all tangled feature pairs
    private ArrayList<FeatureAnnotationToDelete> files;
    // current tangled pair of features
    private int currentIndex;
    private FeatureAnnotationToDelete currentElement;

    JPanel panel;
    GridBagConstraints constraints;
    JButton nextButton;

    public CodeEditorModal(Project project) {
        super(project);
        this.project = project;
        setTitle("Resolve tangling conflicts");
        setSize(700, 700);
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        // create general grid
        panel = new JPanel(new GridBagLayout());
        constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;

        // create next button and add to panel
        nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle the "Next" button click
                onNextButtonClicked();
            }
        });
        constraints.gridy = 9;
        constraints.gridheight = 1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        panel.add(nextButton, constraints);
        return panel;
    }

    // create custom editor text area for 1st file
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

    // create custom editor text area for 2nd file
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

    // add label at the top of the modal
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
                currentElement.getTangledFeatureLPQ() + " in file " + result + ". <br/>"
                + "Please untangle the features to proceed with deletion. </div></html>";
        titleLabel = new JLabel(text);

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

    // every time next button is clicked,
    // go to next element on the file list,
    // create new file editors and fill them with new data
    private void onNextButtonClicked() {
        if (currentIndex < this.files.size()-1) {
            currentIndex += 1;
            currentElement = this.files.get(currentIndex);

            createNewTitle();
            setCodeTextArea();
            if (currentIndex == this.files.size()-1)  { // if last disable next button
                nextButton.setEnabled(false);
            }
        } else {
            nextButton.setEnabled(false);
        }
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
        // Set the location of the dialog to be centered on the screen
        centerRelativeToParent();
    }

    // update text in code text area and highlight our feature
    private void updateCodeTextArea(CustomEditorField codeTextArea, Document document, int start, int end) {
        codeTextArea.setText(document.getText());
        codeTextArea.setDocument(document);
        codeTextArea.setFileType(PsiDocumentManager.getInstance(project).getPsiFile(document).getFileType());

        codeTextArea.getEditor().getCaretModel().moveToOffset(0);
        codeTextArea.selectText(start, end);
    }

    public void setCodeTextArea() {
        // creates 1 window if both annotation types are code and file is the same
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
        // otherwise creates 2 windows
        } else {
            createNewCodeTextArea1(false);
            createNewCodeTextArea2();

            if (currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.code) &&
                    currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.file)) {

                updateCodeTextArea(codeTextArea1, currentElement.getDocument(),
                        currentElement.getDocument().getLineStartOffset(currentElement.getStartLine()),
                        currentElement.getDocument().getLineEndOffset(currentElement.getEndLine()));

                createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(currentElement.getDocument()).getPath());

                ArrayList<Object> tangled = getFeatureToFileOrFolderDocument(currentElement.getTangledFeatureLPQ());
                Document doc = (Document) tangled.get(0);
                updateCodeTextArea(codeTextArea2, doc, (int) tangled.get(1), (int) tangled.get(2));
                createHeader2(currentElement.getTangledFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());

            } else if (currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.file) &&
                    currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.code) ) {

                ArrayList<Object> tangled = getFeatureToFileOrFolderDocument(currentElement.getMainFeatureLPQ());
                Document doc = (Document) tangled.get(0);
                updateCodeTextArea(codeTextArea1, doc, (int) tangled.get(1), (int) tangled.get(2));

                createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());

                updateCodeTextArea(codeTextArea2, currentElement.getDocument(),
                        currentElement.getDocument().getLineStartOffset(currentElement.getStartLine()),
                        currentElement.getDocument().getLineEndOffset(currentElement.getEndLine()));

                createHeader2(currentElement.getTangledFeatureLPQ(), FileDocumentManager.getInstance().getFile(currentElement.getDocument()).getPath());

            } else if (currentElement.getTangledAnnotationType().equals(FeatureFileMapping.AnnotationType.file) &&
                    currentElement.getMainAnnotationType().equals(FeatureFileMapping.AnnotationType.file)) {
                ArrayList<Object> tangled = getFeatureToFileOrFolderDocument(currentElement.getMainFeatureLPQ());
                Document doc = (Document) tangled.get(0);
                updateCodeTextArea(codeTextArea1, doc, (int) tangled.get(1), (int) tangled.get(2));
                createHeader1(currentElement.getMainFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc).getPath());

                ArrayList<Object> tangled2 = getFeatureToFileOrFolderDocument(currentElement.getTangledFeatureLPQ());
                Document doc2 = (Document) tangled2.get(0);
                updateCodeTextArea(codeTextArea2, doc2, (int) tangled2.get(1), (int) tangled2.get(2));
                createHeader2(currentElement.getTangledFeatureLPQ(), FileDocumentManager.getInstance().getFile(doc2).getPath());

            }
        }

    }

    public void setFileList(Set<FeatureAnnotationToDelete> filesSet) {
        ArrayList<FeatureAnnotationToDelete> files = new ArrayList<>(filesSet);

        this.files = files;
        this.currentIndex = 0;
        currentElement = this.files.get(currentIndex);

        createNewTitle();
        setCodeTextArea();
    }

    // find feature-to-folder or feature-to-file file if our feature has annotation type FILE
    // returns ArrayList
    //      0: found document,
    //      1: start offset of our feature to be highlighted,
    //      2: end offset
    private ArrayList<Object> getFeatureToFileOrFolderDocument(String featureLpq) {
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

        return result;
    }
}

// custom class for creating text area with contents of the file
// allows to edit original file
class CustomEditorField extends LanguageTextField  {
    EditorEx editor;
    Document document;
    Project project;


    public CustomEditorField(Language language, Project project) {

        super(language, project, "");
        this.project = project;
        this.setOneLineMode(false);
        this.setEnabled(true);
        this.setPreferredSize(new Dimension(600, 298));
        this.setViewer(false);

        createEditor();
    }

    @Override
    protected @NotNull EditorEx createEditor() {
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
        editor.setEmbeddedIntoDialogWrapper(true);

        EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        editor.setColorsScheme(colorsScheme);

        return editor;
    }

    @Override
    public void setDocument(Document document) {
        this.document = document;
        super.setDocument(document);
    }

    @Nullable
    @Override
    public EditorEx getEditor() {
        return this.editor;
    }

    public void selectText(int startOffset, int endOffset) {
        editor.getSelectionModel().setSelection(startOffset, endOffset);
        editor.getCaretModel().moveToOffset(startOffset);
        editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
    }
}

