package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.LightPlatformCodeInsightTestCase;
import com.intellij.testFramework.VfsTestUtil;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;
import java.io.IOException;
import java.util.regex.Pattern;

public class CloneMethodTest extends LightPlatformCodeInsightTestCase {
    @Override
    public void setUp() throws Exception {
        super.setUp();
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        VfsTestUtil.createFile(getProject().getBaseDir(), ".feature-model");
    }
    public void testCloneMethod() throws IOException {
        String content = """
                public class TestClass {
                    public void exampleMethod() {
                        int x = 10;<caret>
                        System.out.println(x);
                    }
                }
                """;
        prepareFile(content, "TestClass.java");
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        copyMethodAtCaret();
        pasteMethodAtEndOfFile();
        verifyCloneResult();
        verifyTraceParsing();
    }

    private void prepareFile(String text, String fileName) {
        configureFromFileText(fileName, text);
    }

    private void copyMethodAtCaret() {
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

    private void verifyCloneResult() throws IOException {
        String file = getProject().getBasePath() + "/.trace-db.txt";
        VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
        String content = VfsUtilCore.loadText(traceFile);
        assertNotNull("Trace File was not created", traceFile);
        assertTrue("Trace was not stored", !content.isEmpty());
    }
    private void verifyTraceParsing() throws IOException {
        String file = getProject().getBasePath() + "/.trace-db.txt";
        VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
        String content = VfsUtilCore.loadText(traceFile);
        String[] lines = content.split("\n");
        Pattern pattern = Pattern.compile("^[^;]+;[^;]+(?:;[^;]+)?$");

        for (String line : lines) {
            line = line.trim();
            if(line.equals(""))
                continue;
            boolean matches = pattern.matcher(line).matches();
            assertTrue("Line does not match the expected format: " + line, matches);
        }
    }
}
