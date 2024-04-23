package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;
import com.intellij.testFramework.VfsTestUtil;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;

public class CloneMethodTest extends LightPlatformCodeInsightTestCase {
    public void testCopyJavaMethod() throws IOException {
        String content = """
                public class TestClass {
                    public void exampleMethod() {
                        int x = 10;<caret>
                        System.out.println(x);
                    }
                }
                """;
        prepareJava(content, "TestClass.java");
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        copyJavaMethodAtCaret();
        pasteMethodAtEndOfFile();
        String expectedMethod = """
                public void exampleMethod() {
                    int x = 10;
                    System.out.println(x);
                }
                """;
        verifyCopyResult(expectedMethod.trim());
    }
    private void prepareJava(String text, String fileName) {
        configureFromFileText(fileName, text);
    }

    private void copyJavaMethodAtCaret() {
        Editor editor = getEditor();
        PsiFile file = getFile();
        int caretOffset = editor.getCaretModel().getOffset();

        PsiElement elementAtCaret = file.findElementAt(caretOffset);
        PsiMethod methodAtCaret = PsiTreeUtil.getParentOfType(elementAtCaret, PsiMethod.class);

        if (methodAtCaret != null) {
            editor.getSelectionModel().setSelection(methodAtCaret.getTextRange().getStartOffset(),
                    methodAtCaret.getTextRange().getEndOffset());
            executeAction("EditorCopy");
        } else {
            fail("No method found at the caret position");
        }
    }

    private void pasteMethodAtEndOfFile() {
        Editor editor = getEditor();
        PsiFile file = getFile();
        editor.getCaretModel().moveToOffset(file.getTextRange().getEndOffset());
        executeAction("EditorPaste");
    }

    private void verifyCopyResult(String expected) throws IOException {
        String clipboardContent = CopyPasteManager.getInstance().getContents(DataFlavor.stringFlavor);
        String file = getProject().getBasePath() + "/.trace-db.txt";
        VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
        String content = VfsUtilCore.loadText(traceFile);
        assertNotNull("Trace File was not created", traceFile);
        assertTrue("Trace was not stored", !content.isEmpty());
    }
}
