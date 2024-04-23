package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;
import se.isselab.HAnS.assetsManagement.cloneManagement.NotificationProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NotificationProvideTests extends BasePlatformTestCase {
    //VirtualFile traceFile;
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        //traceFile = VfsTestUtil.createFile(myFixture.getProject().getBaseDir(), ".trace-db.txt");
    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/cloneTestData";
    }

    @Test
    public void testGetTraces(){
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
                settingsState.prefKey = "All";
                myFixture.configureByFile("CloneFile.java");
                myFixture.performEditorAction(IdeActions.ACTION_COPY);
                myFixture.performEditorAction(IdeActions.ACTION_PASTE);
                String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
                VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
                List<List<String>> traces = NotificationProvider.getTraces();
                assertTrue(!traces.isEmpty());
                traceFile.delete(CloneTracingTests.class);
            } catch (Exception e) {
                fail("Failed to create files or copy content: " + e.getMessage());
            }
        });
    }
    @Test
    public void testGetLastModificationTime() throws Exception {
        VirtualFile file = myFixture.copyFileToProject("CloneFile.java");
        String lastModificationTime = NotificationProvider.getLastModificationTime(file);
        assertNotNull(lastModificationTime);
    }
    @Test
    public void testIsCloned() throws Exception {
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String content = "public class cloneFile {\n" +
                "    String test = \"test\"; // &line[Test]\n" +
                "    }\n" +
                "}";
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceFile.setBinaryContent(content.getBytes());
                VirtualFile copiedFile = sourceFile.copy(this, destDir, sourceFile.getName());
                //VirtualFile traceFile = VfsUtil.refreshAndFindChild(myFixture.getProject().getBaseDir(), ".trace-db.txt");
                ///boolean isCloned = NotificationProvider.isCloned(copiedFile);
                //assertTrue(isCloned);
                //traceFile.delete(CloneTracingTests.class);
            } catch (Exception e) {
                fail("Failed to create files: " + e.getMessage());
            }
        });

        RefreshQueue.getInstance().refresh(true, true, ()-> {
            String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
            VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
            VirtualFile copiedFile = VfsTestUtil.findFileByCaseSensitivePath(destDir.getPath() + "source.java");
            boolean isCloned = NotificationProvider.isCloned(copiedFile);
            assertTrue(isCloned);
        });
    }

    @Test
    public void testGetSourcePath() throws IOException {
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String content = "public class cloneFile {\n" +
                "    String test = \"test\"; // &line[Test]\n" +
                "    }\n" +
                "}";
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        ApplicationManager.getApplication().invokeAndWait(()-> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    sourceFile.setBinaryContent(content.getBytes());
                    sourceFile.copy(this, destDir, sourceFile.getName());
                } catch (Exception e) {
                    fail("Failed to create files or copy content: " + e.getMessage());
                }
            });
        });
        RefreshQueue.getInstance().refresh(true, true, ()->{
            String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
            VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
            assertNotNull(traceFile);
            VirtualFile copiedFile = VfsUtil.refreshAndFindChild(destDir, "source.java");
            String path = NotificationProvider.getSourcePath(copiedFile);
            assertNotNull("Path should not be null", path);
        });
    }

    private VirtualFile waitForFile(VirtualFile directory, String fileName, int timeoutMillis) {
        final long deadline = System.currentTimeMillis() + timeoutMillis;
        VirtualFile file;
        while ((file = directory.findChild(fileName)) == null && System.currentTimeMillis() < deadline) {
            directory.refresh(false, false);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return file;
    }
    @Test
    public void testisSourceFileChanged() throws Exception{
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String content = "public class cloneFile {\n" +
                "    String test = \"test\"; // &line[Test]\n" +
                "    }\n" +
                "}";
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceFile.setBinaryContent(content.getBytes());
                VirtualFile copiedFile = sourceFile.copy(this, destDir, sourceFile.getName());
                Thread.sleep(1000);
                sourceFile.setBinaryContent("newContent".getBytes());
                VirtualFile traceFile = VfsUtil.refreshAndFindChild( myFixture.getProject().getBaseDir(), ".trace-db.txt");
                boolean isSourceFileChanged = NotificationProvider.isSourceFileChanged(copiedFile);
                assertTrue(isSourceFileChanged);
                traceFile.delete(CloneTracingTests.class);
            } catch (Exception e) {
                fail("Failed to create files or copy content: " + e.getMessage());
            }
        });

    }


    private VirtualFile addChild(VirtualFile dir, String name, boolean directory) throws IOException {
        return WriteAction.computeAndWait(() -> {
            if (directory) {
                return dir.createChildDirectory(this, name);
            } else {
                return dir.createChildData(this, name);
            }
        });
    }

    private VirtualFile createDir() throws IOException {
        File dir = Files.createTempDirectory("vDir").toFile();
        VirtualFile vDir = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(dir);
        if (vDir != null) {
            vDir.getChildren();
            return vDir;
        } else {
            throw new IOException("Failed to create or find virtual directory.");
        }
    }
}
