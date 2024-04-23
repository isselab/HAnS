package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretAction;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;
import se.isselab.HAnS.assetsManagement.cloneManagement.FeaturesAnnotationsExtractor;
import se.isselab.HAnS.assetsManagement.cloneManagement.TracingHandler;

import java.net.CacheRequest;
import java.util.List;
import java.util.regex.Pattern;

public class CloneTracingTests extends BasePlatformTestCase {
    TracingHandler tracingHandler;
    VirtualFile traceFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tracingHandler = new TracingHandler();
        //traceFile = VfsTestUtil.createFile(myFixture.getProject().getBaseDir(), ".trace-db.txt");

    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/cloneTestData";
    }

    @Test
    public void testGetCurrentDateAndTime() {
        TracingHandler tracingHandler = new TracingHandler();
        String dateTime = tracingHandler.getCurrentDateAndTime();
        String regex = "\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        assertTrue("The date and time format should match yyyyMMddHHmmss",pattern.matcher(dateTime).matches());
    }
    @Test
    public void testGetTraceFilePath() {
        String traceFilePath = TracingHandler.getTraceFilePath(myFixture.getProject());
        assertNotNull("Trace file path was not found", traceFilePath);
    }
    @Test
    public void testGetRelativePath() {
        String absolutePathDifferentProjectName = "/users/example/ExampleProject/src/dir";
        String absolutePathSameProjectName = "GetRelativePath/src/dir";
        String projectName = "ExampleProject";
        String resultDifferentProjectNames = new TracingHandler().getRelativePath(myFixture.getProject(), absolutePathDifferentProjectName, projectName);
        String resultSametProjectNames = new TracingHandler().getRelativePath(myFixture.getProject(), absolutePathSameProjectName, "GetRelativePath");
        assertEquals("ExampleProject/src/dir", resultDifferentProjectNames);
        assertEquals("src/dir", resultSametProjectNames);
    }
    @Test
    public void testFeaturesExtractions() {
        PsiFile file = myFixture.configureByFile("CloneFile.java");
        List<String> listFeatureAnnotations = FeaturesAnnotationsExtractor.extractFeatureNames(file);
        assertTrue(!listFeatureAnnotations.isEmpty());
    }
    @Test
    public void testCloneFragment(){
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
                settingsState.prefKey = "All";
                VirtualFile sourceFile = myFixture.getTempDirFixture().createFile("CloneFile.java", "public class cloneFile {\n" +
                        "    String test = \"test\"; // &line[Test]\n" +
                        "    }\n" +
                        "}");
                myFixture.openFileInEditor(sourceFile);
                myFixture.performEditorAction(IdeActions.ACTION_COPY);
                myFixture.performEditorAction(IdeActions.ACTION_PASTE);
                String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
                VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
                String content = VfsUtilCore.loadText(traceFile);
                assertNotNull("Trace File was not created", traceFile);
                assertTrue("Trace was not stored", !content.isEmpty());
                traceFile.delete(CloneTracingTests.class);
            } catch (Exception e) {
                fail("Failed to create files or copy content: " + e.getMessage());
            }
        });
    }
    @Test
    public void testTraceParsing() {
        ApplicationManager.getApplication().runWriteAction(() -> {
        try {
            CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
            settingsState.prefKey = "All";
            myFixture.configureByFile("CloneFile.java");
            myFixture.performEditorAction(IdeActions.ACTION_COPY);
            myFixture.performEditorAction(IdeActions.ACTION_PASTE);
            String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
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
            traceFile.delete(CloneTracingTests.class);
        } catch (Exception e) {
            fail("Failed to create files or copy content: " + e.getMessage());
        }
        });
    }

    @Test
    public void testCloneMethod() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
                settingsState.prefKey = "All";
                myFixture.configureByFile("CloneFile.java");
                PsiFile file = myFixture.getFile();
                CaretModel caretModel = myFixture.getEditor().getCaretModel();
                Caret carett = caretModel.getCurrentCaret();
                int offset = carett.getOffset();


                // Action to perform at each caret
                CaretAction myCaretAction = caret -> {
                    PsiMethod method = PsiTreeUtil.findChildOfType(file, PsiMethod.class);
                    if (method != null) {
                        myFixture.getEditor().getSelectionModel().setSelection(method.getTextRange().getStartOffset(),
                                method.getTextRange().getEndOffset());
                        EditorActionHandler copyHandler = EditorActionManager.getInstance().getActionHandler("EditorCopy");
                        copyHandler.execute(myFixture.getEditor(), caret, null);
                        System.out.println("Copied method: " + method.getName());
                    }
                };
                caretModel.runForEachCaret(myCaretAction);
                String tracefile = myFixture.getProject().getBasePath() + "/.trace-db.txt";
                VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(tracefile);
                String content = VfsUtilCore.loadText(traceFile);
                assertNotNull("Trace File was not created", traceFile);
                assertTrue("Trace was not stored", !content.isEmpty());
                traceFile.delete(CloneTracingTests.class);
            } catch (Exception e) {
                fail("Failed to create files or copy content: " + e.getMessage());
            }
        });
    }
}

