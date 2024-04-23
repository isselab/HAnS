package se.isselab.HAnS.assetsManagementTests.cloneAssetsTests;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.VfsTestUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.Before;
import org.junit.Test;
import se.isselab.HAnS.assetsManagement.CloneManagementSettingsState;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CloneVfsTests extends BasePlatformTestCase {
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }
    @Test
    public void testCopyPasteEvent() throws IOException {
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
                sourceFile.copy(this, destDir, sourceFile.getName());
                VirtualFile copiedFile = destDir.findChild(sourceFile.getName());
                String actualContent = new String(copiedFile.contentsToByteArray());
                assertTrue("File not found",copiedFile != null);
                assertEquals("The file name should match the source file name.", sourceFile.getName(), copiedFile.getName());
                assertEquals("The file content should match the expected content.", content, actualContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
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
