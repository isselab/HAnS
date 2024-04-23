package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
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

public class NotificationProviderTests extends BasePlatformTestCase {
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        CloneManagementSettingsState settingsState = CloneManagementSettingsState.getInstance();
        settingsState.prefKey = "All";
        VfsTestUtil.createFile(getProject().getBaseDir(), ".feature-model");
    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/assetsManagementTestData/cloneAssetsTestData";
    }

    @Test
    public void testGetTraces(){
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                myFixture.configureByFile("CloneFile.java");
                myFixture.performEditorAction(IdeActions.ACTION_COPY);
                myFixture.performEditorAction(IdeActions.ACTION_PASTE);
                String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
                VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
                List<List<String>> traces = NotificationProvider.getTraces();
                assertTrue(!traces.isEmpty());
                traceFile.delete(TracingTests.class);
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
        String content = myFixture.configureByFile("CloneFile.java").getText();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceFile.setBinaryContent(content.getBytes());
                VirtualFile copiedFile = sourceFile.copy(this, destDir, sourceFile.getName());
                VfsUtil.refreshAndFindChild(myFixture.getProject().getBaseDir(), ".trace-db.txt");
                boolean isCloned = NotificationProvider.isCloned(copiedFile);
                assertTrue(isCloned);
            } catch (Exception e) {
                fail("Failed to create files: " + e.getMessage());
            }
        });
    }

    @Test
    public void testGetSourcePath() throws IOException {
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String content = myFixture.configureByFile("CloneFile.java").getText();
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

    @Test
    public void testisSourceFileChanged() throws Exception{
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String content = myFixture.configureByFile("CloneFile.java").getText();
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                sourceFile.setBinaryContent(content.getBytes());
                VirtualFile copiedFile = sourceFile.copy(this, destDir, sourceFile.getName());
                Thread.sleep(1000);
                sourceFile.setBinaryContent("newContent".getBytes());
                VirtualFile traceFile = VfsUtil.refreshAndFindChild( myFixture.getProject().getBaseDir(), ".trace-db.txt");
                boolean isSourceFileChanged = NotificationProvider.isSourceFileChanged(copiedFile);
                assertTrue(isSourceFileChanged);
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
