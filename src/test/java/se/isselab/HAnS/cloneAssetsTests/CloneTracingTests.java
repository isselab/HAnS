package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.EditorTestUtil;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsComponent;
import se.isselab.HAnS.assetsManagement.HansAssetsManagementPage;
import se.isselab.HAnS.assetsManagement.cloneManagement.TracingHandler;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

public class CloneTracingTests extends BasePlatformTestCase {
    TracingHandler tracingHandler;
    HansAssetsManagementPage hansAssetsManagementPage;
    JComponent settings;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tracingHandler = new TracingHandler();
        hansAssetsManagementPage = new HansAssetsManagementPage();
        settings = hansAssetsManagementPage.createComponent();
    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/cloneTestData";
    }
    public void testGetCurrentDateAndTime() {
        TracingHandler tracingHandler = new TracingHandler();
        String dateTime = tracingHandler.getCurrentDateAndTime();
        String regex = "\\d{4}\\d{2}\\d{2}\\d{2}\\d{2}\\d{2}";
        Pattern pattern = Pattern.compile(regex);
        assertTrue("The date and time format should match yyyyMMddHHmmss",pattern.matcher(dateTime).matches());
    }
    public void testGetTraceFilePath() {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile[] vFiles = ProjectRootManager.getInstance(myFixture.getProject()).getContentRoots();
                VirtualFile traceFile = VfsTestUtil.createFile(vFiles[0], ".trace-db.txt");
                String traceFilePath = TracingHandler.getTraceFilePath(myFixture.getProject());
                assertNotNull("Trace file path was not found", traceFilePath);
            } catch (Exception e) {
                fail("Failed to create src directory or .trace-db.txt file");
            }
        });
    }
    public void testStoreCloneTrace(){
        VirtualFile fileToCopy = myFixture.configureByFile("CloneFile.java").getVirtualFile();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                VirtualFile[] vFiles = ProjectRootManager.getInstance(myFixture.getProject()).getContentRoots();
                VirtualFile traceFile = VfsTestUtil.createFile(vFiles[0], ".trace-db.txt");
                VfsUtil.saveText(traceFile, "content");
                traceFile.refresh(false, false);
                //tracingHandler.storeCloneTrace(myFixture.getProject(), myFixture.getProject().getName(), fileToCopy.getPath(), fileToCopy.getPath());
                String content = VfsUtilCore.loadText(traceFile);
                assertTrue("Trace was not stored", !content.isEmpty());
            } catch (Exception e) {
                fail("Failed to create src directory or .trace-db.txt file");
            }
        });
    }

    public void testCloneFile(){
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                Project project = myFixture.getProject();
                VirtualFile[] vFiles = ProjectRootManager.getInstance(project).getContentRoots();
                VirtualFile dir1 = VfsTestUtil.createDir(vFiles[0], "dir1");
                VirtualFile dir2 = VfsTestUtil.createDir(vFiles[0], "dir2");
                VirtualFile fileInDir1 = VfsTestUtil.createFile(dir1, "testFile.java", "" +
                        "public class cloneFile {\n" +
                        "    String test = \"test\"; // &line[Test]\n" +
                        "}");
                hansAssetsManagementPage.getCloneManagementSettingsComponent().setAssetsManagementPrefKey("All");
                //VirtualFile copiedFile = fileInDir1.copy(this, dir2, "copiedFile.java");
                VirtualFile copiedFile = myFixture.copyFileToProject("CloneFile.java");
                VirtualFile traceFile = VirtualFileManager.getInstance().findFileByUrl(vFiles[0] + "/.trace-db.txt");
                assertNotNull("trace file not created", traceFile);
                assertTrue("Directory 2 should contain the copied file", Arrays.asList(dir2.getChildren()).contains(copiedFile));
                String content = VfsUtilCore.loadText(copiedFile);
                assertEquals("public class cloneFile {\n" +
                        "    String test = \"test\"; // &line[Test]\n" +
                        "}", content);

            } catch (IOException e) {
                fail("IOException during test: " + e.getMessage());
            }
        });
    }

    public void testCloneFragment(){
        WriteCommandAction.runWriteCommandAction(getProject(), () -> {
            try {
                VirtualFile[] vFiles = ProjectRootManager.getInstance(myFixture.getProject()).getContentRoots();
                VirtualFile sourceFile = myFixture.getTempDirFixture().createFile("source.java", "public class cloneFile {\\n\" +\n" +
                        "                        \"    String test = \\\"test\\\"; // &line[Test]\\n\" +\n" +
                        "                        \"}");
                assertNotNull(sourceFile);
                myFixture.createFile(".trace-db.txt", "");
                myFixture.openFileInEditor(sourceFile);
                Editor editor = myFixture.getEditor();
                Document document = editor.getDocument();
                editor.getSelectionModel().setSelection(0, document.getTextLength());
                myFixture.performEditorAction(IdeActions.ACTION_COPY);
                myFixture.performEditorAction(IdeActions.ACTION_PASTE);
                VirtualFile traceFile = VirtualFileManager.getInstance().findFileByUrl(vFiles[0] + "/.trace-db.txt");
                assertNotNull("trace file not created", traceFile);
            } catch (Exception e) {
                fail("Failed to create files or copy content: " + e.getMessage());
            }
        });
    }

}
