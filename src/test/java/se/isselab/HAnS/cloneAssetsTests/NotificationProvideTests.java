package se.isselab.HAnS.cloneAssetsTests;

import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.cloneManagement.NotificationProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class NotificationProvideTests extends BasePlatformTestCase {
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }
    @Override
    protected String getTestDataPath() {
        return "src/test/resources/cloneTestData";
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
    public void testGetSourcePath(){
        VirtualFile sourceDir = createDir();
        VirtualFile destDir = createDir();
        VirtualFile sourceFile = addChild(sourceDir, "source.java", false);
        String content = "public class cloneFile {\n" +
                "    String test = \"test\"; // &line[Test]\n" +
                "    }\n" +
                "}";
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                //TODO create vfs copy event
                sourceFile.setBinaryContent(content.getBytes());
                sourceFile.copy(this, destDir, sourceFile.getName());
                VirtualFile copiedFile = destDir.findChild(sourceFile.getName());
                String actualContent = new String(copiedFile.contentsToByteArray());
                String file = myFixture.getProject().getBasePath() + "/.trace-db.txt";
                VirtualFile traceFile = VfsTestUtil.findFileByCaseSensitivePath(file);
                String path = NotificationProvider.getSourcePath(myFixture.copyFileToProject("CloneFile.java"));
                assertNotNull(path);
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
