package se.isselab.HAnS.actions;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import se.isselab.HAnS.featureLocation.FeatureFileMapping;
import se.isselab.HAnS.featureLocation.FeatureLocation;
import se.isselab.HAnS.featureLocation.FeatureLocationBlock;
import se.isselab.HAnS.featureModel.psi.FeatureModelFeature;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class RemoveFeatureAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) return;

        PsiManager psiManager = PsiManager.getInstance(project);
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();

        //search for java files
        Collection<VirtualFile> javaFiles = FileTypeIndex.getFiles(fileTypeManager.getFileTypeByExtension("java"),
                GlobalSearchScope.projectScope(project));

        for (VirtualFile virtualFile : javaFiles) {
            PsiFile openedFile = psiManager.findFile(virtualFile);
            if (openedFile == null) continue;
            // get the document associated with current psiDile (java file)
            Document document = PsiDocumentManager.getInstance(project).getDocument(openedFile);
            if (document == null) continue;

            // traverse PSI of file
            openedFile.accept(new PsiRecursiveElementVisitor() {

                // Reference to keep track of the state of the comment
                final AtomicReference<PsiComment> beginReference = new AtomicReference<>();


                @Override
                public void visitComment(@NotNull PsiComment comment) {
                    if (e.getData(LangDataKeys.PSI_ELEMENT) instanceof FeatureModelFeature) {
                        FeatureModelFeature feature = (FeatureModelFeature) e.getData(LangDataKeys.PSI_ELEMENT);
                        String featureName = feature.getText();
                        String[] featureNames = featureName.split("\\R");

                        // Obtain the FeatureFileMapping instance associated with the feature
                        FeatureFileMapping featureMapping = new FeatureFileMapping(feature);

                        // Get the set of file paths associated with the feature
                        Set<String> filePaths = featureMapping.getMappedFilePaths();

                        // Iterate over each file path
                        for (String filePath : filePaths) {
                            // Retrieve the FeatureLocation instance for the current file path
                            FeatureLocation featureLocation = featureMapping.getFeatureLocationsForFile(filePath);
                            if (featureLocation == null) continue;

                            // Iterate over each block in the FeatureLocation
                            for (FeatureLocationBlock block : featureLocation.getFeatureLocations()) {
                                // Check if the block has both begin and end annotations
                                if (block.getStartLine() != -1 && block.getEndLine() != -1) {
                                    // Remove the block of code between the begin and end annotations
                                    deleteCodeBlock(project, document, block.getStartLine(), block.getEndLine());

                                    feature.deleteFeature();
                                }
                            }
                        }

                        for (String name : featureNames) {
                            String featureWithoutSpaces = name.replaceAll("\\s+", "");
                            if (featureWithoutSpaces.isEmpty()) continue;

                            String commentText = comment.getText();

                            if (commentText.contains(featureWithoutSpaces)) {
                                int lineNumber = document.getLineNumber(comment.getTextRange().getStartOffset() + 1);
                                System.out.println("Asset in " + openedFile.getName() + "  at line number " + (lineNumber + 1));

                                /*
                                // Search for begin and end annotations
                                if (commentText.contains("&begin")) {
                                    beginReference.set(comment);
                                    System.out.println("Deleting //&begin comment: " + commentText);
                                     // Print debug information
                                    if (commentText.contains("&end")) {
                                        // Print debug information
                                        System.out.println("Deleting //&end comment: " + commentText);
                                        PsiComment beginComment = beginReference.get();
                                        if (beginComment != null) {
                                            // Get the line numbers for the begin and end comments
                                            int lineStartOffset = document.getLineStartOffset(document.getLineNumber(comment.getTextOffset()));
                                            int lineEndOffset = document.getLineEndOffset(document.getLineNumber(comment.getTextOffset() + comment.getTextLength())-1);
                                            ApplicationManager.getApplication().invokeLater(() -> {
                                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                                    document.deleteString(lineStartOffset, lineEndOffset);
                                                });
                                            });
                                        }
                                    }
                                } */
                                    //else{
                                        // Search for line annotation
                                        if (comment.getText().contains("&line")) {
                                            // Delete the line from the document
                                            int lineStartOffset = document.getLineStartOffset(document.getLineNumber(comment.getTextOffset()));
                                            int lineEndOffset = document.getLineEndOffset(document.getLineNumber(comment.getTextOffset() + comment.getTextLength() - 1));
                                            ApplicationManager.getApplication().invokeLater(() -> {
                                                WriteCommandAction.runWriteCommandAction(project, () -> {
                                                    document.deleteString(lineStartOffset, lineEndOffset);
                                                });
                                            });

                                            // Delete from feature model with children optionally
                                            feature.deleteFeature();
                                        }

                                 //   }
                            }
                        }
                    }
                }
            });
        }
    }

    private void deleteCodeBlock(Project project, Document document, int beginLineNumber, int endLineNumber) {
        // Get the start and end offsets of the block of code
        int beginOffset = document.getLineStartOffset(beginLineNumber);
        int endOffset = document.getLineEndOffset(endLineNumber);

        // Delete the block of code from the document
        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                document.deleteString(beginOffset, endOffset);
            });
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setEnabledAndVisible(project != null);
    }
}
